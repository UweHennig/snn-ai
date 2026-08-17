/**
 * @(#)NeuronFieldModel.java
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
 * NeuronFieldModel
 *
 * @author Uwe Hennig
 */
public final class NeuronFieldModel {
    public final int   capacity;
    public final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        JAVA_INT.withName("type"),
        JAVA_INT.withName("fieldId"),
        JAVA_INT.withName("neuronRef"),
        JAVA_INT.withName("outNeighboursRef"),
        JAVA_INT.withName("inNeighboursRef")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));

    static final VarHandle VH_TYPE =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("type"));

    static final VarHandle VH_FIELD_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fieldId"));
    static final VarHandle VH_NEURON_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("neuronRef"));
    static final VarHandle VH_OUT_NEIGHBOURS_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("outNeighboursRef"));
    static final VarHandle VH_IN_NEIGHBOURS_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("inNeighboursRef"));
    // @formatter:on

    // ----- public -----

    public NeuronFieldModel(int capacity) {
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

    // ----- lock/unlock -----

    private static final int WRITER_WAITING = 0x40000000; // Bit 30
    private static final int WRITER_ACTIVE  = 0xFFFFFFFF; // -1

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

            // no reader active and write flag is set or initial state.
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


    // ----- getter/setter -----

    int getType(int index) {
        return (int) VH_TYPE.get(segment, 0L, index);
    }

    void setType(int index, int value) {
        VH_TYPE.set(segment, 0L, index, value);
    }

    int getNeuronRef(int index) {
        return (int)VH_NEURON_REF.get(segment, 0L, index);
    }

    void setNeuronRef(int index, int value) {
        VH_NEURON_REF.set(segment, 0L, index, value);
    }

    int getOutNeighboursRef(int index) {
        return (int)VH_OUT_NEIGHBOURS_REF.get(segment, 0L, index);
    }

    void setOutNeighboursRef(int index, int value) {
        VH_OUT_NEIGHBOURS_REF.set(segment, 0L, index, value);
    }

    int getInNeighbourRef(int index) {
        return (int)VH_IN_NEIGHBOURS_REF.get(segment, 0L, index);
    }

    void setInNeighboursRef(int index, int value) {
        VH_IN_NEIGHBOURS_REF.set(segment, 0L, index, value);
    }

    int getFieldId(int index) {
        return (int)VH_FIELD_ID.get(segment, 0L, index);
    }

    void setFieldId(int index, int value) {
        VH_FIELD_ID.set(segment, 0L, index, value);
    }
}
