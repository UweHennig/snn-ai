/**
 * @(#)StimulusModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.LockSupport;

/**
 * StimulusModel
 *
 * @author Uwe Hennig
 */
@Deprecated
public class StimulusModel {
    @Deprecated
    final int   capacity;
    @Deprecated
    final Arena arena;

    @Deprecated
    SequenceLayout sequenceLayout;
    @Deprecated
    MemorySegment  segment;

    // @formatter:off
    @Deprecated
    public static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        MemoryLayout.paddingLayout(4),
        JAVA_INT.withName("stimulusType"),  // FEEDBACK_TIME, FEEDBACK_VALUE, STIMULUS
        JAVA_INT.withName("transferType"),  // REZEPTOR, EFFECTOR, AXON
        JAVA_INT.withName("targetRef"),     // MATRIX_REF, AXON_SYNAPSE_REF, REZEPTOR_REF, DENDRIT_REF, ...
        JAVA_INT.withName("targetSubRef"),  // (x,y) or positin
        JAVA_INT.withName("targetType"),    // Neuronelement
        JAVA_FLOAT.withName("value"),       // delta value or absolute value
        JAVA_LONG.withName("expiry")        // expiry
    ).withByteAlignment(8);

    @Deprecated
    static final VarHandle VH_LOCK           = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));
    @Deprecated
    static final VarHandle VH_STIMULUS_TYPE  = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("stimulusType"));
    @Deprecated
    static final VarHandle VH_TRANSFER_TYPE  = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("transferType"));
    @Deprecated
    static final VarHandle VH_TARGET_REF     = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("targetRef"));
    @Deprecated
    static final VarHandle VH_TARGET_SUB_REF = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("targetSubRef"));
    @Deprecated
    static final VarHandle VH_TARGET_TYPE    = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("targetType"));
    @Deprecated
    static final VarHandle VH_VALUE          = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("value"));
    @Deprecated
    static final VarHandle VH_EXPIRY         = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("expiry"));
    // @formatter:on

    @Deprecated
    public StimulusModel(int capacity) {
        this.capacity = capacity;
        this.arena = Arena.ofShared();

        this.sequenceLayout = MemoryLayout.sequenceLayout(capacity, LAYOUT);
        this.segment = arena.allocate(sequenceLayout);
    }

    @Deprecated
    public void close() {
        arena.close();
    }

    @Deprecated
    public int getCapacity() {
        return capacity;
    }

    // ----- lock/unlock -----

    private static final int WRITER_WAITING = 0x40000000; // Bit 30
    private static final int WRITER_ACTIVE  = 0xFFFFFFFF; // -1

    @Deprecated
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

    @Deprecated
    void writeUnlock(int index) {
        VH_LOCK.setRelease(segment, 0L, index, 0);
    }

    @Deprecated
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

    @Deprecated
    void readUnlock(int index) {
       // decrement reader counter
       VH_LOCK.getAndAdd(segment, 0L, index, -1);
    }

    @Deprecated
    boolean tryWriteLock(long index) {
        return (int) VH_LOCK.compareAndExchange(segment, 0L, index, 0, -1) == 0;
    }

    @Deprecated
    boolean isWriteLocked(long index) {
        return (int) VH_LOCK.get(segment, 0L, index) == -1;
    }

    @Deprecated
    void backoff(int spins) {
        if (spins < 64) {
            Thread.onSpinWait();
        } else {
            LockSupport.parkNanos(1);
        }
    }

    // ----- getter/setter -----

    @Deprecated
    int getStimulusType(int index) {
        return (int) VH_STIMULUS_TYPE.get(segment, 0L, index);
    }

    @Deprecated
    void setStimulusType(int index, int value) {
        VH_STIMULUS_TYPE.set(segment, 0L, index, value);
    }

    @Deprecated
    int getTransferType(int index) {
        return (int) VH_TRANSFER_TYPE.get(segment, 0L, index);
    }

    @Deprecated
    void setTransferType(int index, int value) {
        VH_TRANSFER_TYPE.set(segment, 0L, index, value);
    }

    @Deprecated
    int getTargetRef(int index) {
        return (int) VH_TARGET_REF.get(segment, 0L, index);
    }

    @Deprecated
    void setTargetRef(int index, int value) {
        VH_TARGET_REF.set(segment, 0L, index, value);
    }

    @Deprecated
    int getTargetSubRef(int index) {
        return (int) VH_TARGET_SUB_REF.get(segment, 0L, index);
    }

    @Deprecated
    void setTargetSubRef(int index, int value) {
        VH_TARGET_SUB_REF.set(segment, 0L, index, value);
    }

    @Deprecated
    int getTargetType(int index) {
        return (int) VH_TARGET_TYPE.get(segment, 0L, index);
    }

    @Deprecated
    void setTargetType(int index, int value) {
        VH_TARGET_TYPE.set(segment, 0L, index, value);
    }

    @Deprecated
    float getValue(int index) {
        return (float) VH_VALUE.get(segment, 0L, index);
    }

    @Deprecated
    void setValue(int index, float value) {
        VH_VALUE.set(segment, 0L, index, value);
    }

    @Deprecated
    long getExpiry(int index) {
        return (long) VH_EXPIRY.get(segment, 0L, index);
    }

    @Deprecated
    void setExpiry(int index, long value) {
        VH_EXPIRY.set(segment, 0L, index, value);
    }
}
