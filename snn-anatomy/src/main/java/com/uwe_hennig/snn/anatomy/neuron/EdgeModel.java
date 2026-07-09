/**
 * @(#)EdgeModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.LockSupport;

/**
 * EdgeModel
 *
 * @author Uwe Hennig
 */
public class EdgeModel {
    private static final int MULTI_FLAG     = 0x40000000; // Bit 30
    private static final int WRITER_WAITING = 0x40000000; // Bit 30
    private static final int WRITER_ACTIVE  = 0xFFFFFFFF; // -1

    public final int   capacity;
    public final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        JAVA_INT.withName("srcId"),
        JAVA_INT.withName("srcType"),
        JAVA_INT.withName("trgType"),
        JAVA_INT.withName("trgRef"),
        MemoryLayout.paddingLayout(4)
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));

    static final VarHandle VH_SRC_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("srcId"));
    static final VarHandle VH_SRC_TYPE =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("srcType"));
    static final VarHandle VH_TRG_TYPE =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("trgType"));
    static final VarHandle VH_TRG_REF=
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("trgRef"));
    // @formatter:off


    public EdgeModel(int capacity) {
        assert capacity > 0 : "invalid capacity";

        this.capacity = capacity;
        this.arena = Arena.ofShared();

        this.sequenceLayout = MemoryLayout.sequenceLayout(capacity, LAYOUT);
        this.segment = arena.allocate(sequenceLayout);
    }

    public void close() {
        arena.close();
    }

    public int getCapacity() {
        return capacity;
    }

    // ----- getter/setter -----

    int getSrcId(int index) {
        return (int) VH_SRC_ID.get(segment, 0L, index);
    }

    void setSrcId(int index, int value) {
        VH_SRC_ID.set(segment, 0L, index, value);
    }

    int getSrcType(int index) {
        return (int) VH_SRC_TYPE.get(segment, 0L, index);
    }

    void setSrcType(int index, int value) {
        VH_SRC_TYPE.set(segment, 0L, index, value);
    }

    int getTrgType(int index) {
        return (int) VH_TRG_TYPE.get(segment, 0L, index);
    }

    void setTrgType(int index, int value) {
        VH_TRG_TYPE.set(segment, 0L, index, value);
    }

    int getTrgRef(int index) {
        int raw = (int) VH_TRG_REF.get(segment, 0L, index);
        if ((raw & MULTI_FLAG) != 0) {
            return raw & ~MULTI_FLAG;
        }
        return raw;
    }

    void setSingleTrgRef(int index, int value) {
        VH_TRG_REF.set(segment, 0L, index, value);
    }

    void setMultiTrgRef(int index, int value) {
        VH_TRG_REF.set(segment, 0L, index, value | MULTI_FLAG);
    }

    boolean isMuliTrgRef(int index) {
        int ref = (int) VH_TRG_REF.get(segment, 0L, index);
        return (ref & MULTI_FLAG) > 0;
    }

    // ----- lock/unlock -----

    void writeLock(int index) {
        int spins = 0;
        // set the WRITER_WAITING flag to indicate a write request
        while (true) {
            int current = (int) VH_LOCK.getVolatile(segment, 0L, index);
            if ((current & WRITER_WAITING) == 0) {
                if (VH_LOCK.compareAndSet(segment, 0L, index, current, current | WRITER_WAITING)) {
                    break;
                }
            } else {
                break;
            }
            backoff(spins++);
        }

        // set the lock
        spins = 0;
        while (true) {
            int current = (int) VH_LOCK.getVolatile(segment, 0L, index);

            // no reader aktive and write flag is set or initial state.
            if (current == WRITER_WAITING || current == 0) {
                if (VH_LOCK.compareAndSet(segment, 0L, index, current, WRITER_ACTIVE)) {
                    return;
                }
            }

            backoff(spins++);
        }
    }

    void writeUnlock(int index) {
        VH_LOCK.setRelease(segment, 0L, index, 0);
    }

    boolean readLock(int index) {
        int spins = 0;
        while (true) {
            int current = (int) VH_LOCK.getVolatile(segment, 0L, index);

            // give priority to the writers
            if (current < 0 || (current & WRITER_WAITING) != 0) {
                backoff(spins++);
                continue;
            }

            // increment reader counter
            if (VH_LOCK.compareAndSet(segment, 0L, index, current, current + 1)) {
                return true;
            }
        }
    }

    void readUnlock(int index) {
       // decrement reader counter
       VH_LOCK.getAndAdd(segment, 0L, index, -1);
    }

    void backoff(int spins) {
        if (spins < 64) {
            Thread.onSpinWait();
        } else {
            LockSupport.parkNanos(1);
        }
    }
}
