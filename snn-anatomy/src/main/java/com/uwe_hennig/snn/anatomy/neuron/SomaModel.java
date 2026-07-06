/**
 * @(#)SomaModel.java
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
 * SomaModel
 *
 * @author Uwe Hennig
 */
public final class SomaModel {
    public final int   capacity;
    public final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        JAVA_INT.withName("fieldId"),
        JAVA_INT.withName("neuronId"),
        JAVA_INT.withName("potentialId"),
        JAVA_INT.withName("thresholdId"),
        JAVA_INT.withName("stpId"),
        JAVA_INT.withName("ltpId"),
        JAVA_INT.withName("axonId")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));
    static final VarHandle VH_FIELD_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fieldId"));
    static final VarHandle VH_NEURON_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("neuronId"));
    static final VarHandle VH_POTENTIAL_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("potentialId"));
    static final VarHandle VH_THRESHOLD_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("thresholdId"));
    static final VarHandle VH_STP_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("stpId"));
    static final VarHandle VH_LTP_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("ltpId"));
    static final VarHandle VH_AXON_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("axonId"));
    // @formatter:on

    public SomaModel(int capacity) {
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


    // ----- getter/setter -----

    int getFieldId(int index) {
        return (int) VH_FIELD_ID.get(segment, 0L, index);
    }

    void setFieldId(int index, int value) {
        VH_FIELD_ID.set(segment, 0L, index, value);
    }

    int getNeuronId(int index) {
        return (int) VH_NEURON_ID.get(segment, 0L, index);
    }

    void setNeuronId(int index, int value) {
        VH_NEURON_ID.set(segment, 0L, index, value);
    }

    int getPotentialId(int index) {
        return (int) VH_POTENTIAL_ID.get(segment, 0L, index);
    }

    void setPotentialId(int index, int value) {
        VH_POTENTIAL_ID.set(segment, 0L, index, value);
    }

    int getThresholdId(int index) {
        return (int) VH_THRESHOLD_ID.get(segment, 0L, index);
    }

    void setThresholdId(int index, int value) {
        VH_THRESHOLD_ID.set(segment, 0L, index, value);
    }

    int getStpId(int index) {
        return (int) VH_STP_ID.get(segment, 0L, index);
    }

    void setStpId(int index, int value) {
        VH_STP_ID.set(segment, 0L, index, value);
    }

    int getLtpId(int index) {
        return (int) VH_LTP_ID.get(segment, 0L, index);
    }

    void setLtpId(int index, int value) {
        VH_LTP_ID.set(segment, 0L, index, value);
    }

    int getAxonId(int index) {
        return (int) VH_AXON_ID.get(segment, 0L, index);
    }

    void setAxonId(int index, int value) {
        VH_AXON_ID.set(segment, 0L, index, value);
    }
}
