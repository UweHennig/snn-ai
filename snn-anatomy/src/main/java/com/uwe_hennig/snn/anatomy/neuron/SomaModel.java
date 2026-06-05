/**
 * @(#)SomaModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.invoke.VarHandle;

/**
 * SomaModel
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public final class SomaModel {
    public final long  capacity;
    public final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        MemoryLayout.paddingLayout(4),
        JAVA_LONG.withName("fieldId"),
        JAVA_LONG.withName("neuronId"),
        JAVA_LONG.withName("potentialId"),
        JAVA_LONG.withName("thresholdId"),
        JAVA_LONG.withName("stpId"),
        JAVA_LONG.withName("ltpId")
    );

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
    // @formatter:on

    public SomaModel(long capacity) {
        assert capacity > 0 : "invalid capacity";

        this.capacity = capacity;
        this.arena = Arena.ofShared();

        this.sequenceLayout = MemoryLayout.sequenceLayout(capacity, LAYOUT);
        this.segment = arena.allocate(sequenceLayout);
    }

    public void close() {
        arena.close();
    }

    public long getCapacity() {
        return capacity;
    }

    // ----- lock/unlock -----

    void lock(long index) {
        // 0 = unlocked, 1 = lock
        while (!VH_LOCK.compareAndSet(segment, 0L, index, 0, 1)) {
            Thread.onSpinWait();
        }
    }

    void unlock(long index) {
        VH_LOCK.setRelease(segment, 0L, index, 0);
    }

    // ----- getter/setter -----

    long getFieldId(long index) {
        return (long) VH_FIELD_ID.get(segment, 0L, index);
    }

    void setFieldId(long index, long value) {
        VH_FIELD_ID.set(segment, 0L, index, value);
    }

    long getNeuronId(long index) {
        return (long) VH_NEURON_ID.get(segment, 0L, index);
    }

    void setNeuronId(long index, long value) {
        VH_NEURON_ID.set(segment, 0L, index, value);
    }

    long getPotentialId(long index) {
        return (long) VH_POTENTIAL_ID.get(segment, 0L, index);
    }

    void setPotentialId(long index, long value) {
        VH_POTENTIAL_ID.set(segment, 0L, index, value);
    }

    long getThresholdId(long index) {
        return (long) VH_THRESHOLD_ID.get(segment, 0L, index);
    }

    void setThresholdId(long index, long value) {
        VH_THRESHOLD_ID.set(segment, 0L, index, value);
    }

    long getStpId(long index) {
        return (long) VH_STP_ID.get(segment, 0L, index);
    }

    void setStpId(long index, long value) {
        VH_STP_ID.set(segment, 0L, index, value);
    }

    long getLtpId(long index) {
        return (long) VH_LTP_ID.get(segment, 0L, index);
    }

    void setLtpId(long index, long value) {
        VH_LTP_ID.set(segment, 0L, index, value);
    }
}
