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
        MemoryLayout.paddingLayout(4),
        JAVA_INT.withName("fieldId"),
        JAVA_INT.withName("neuronId"),
        JAVA_INT.withName("potentialId"),
        JAVA_INT.withName("thresholdId"),
        JAVA_INT.withName("stpId"),
        JAVA_INT.withName("ltpId")
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

    void lock(int index) {
        // 0 = unlocked, 1 = lock
        while (!VH_LOCK.compareAndSet(segment, 0L, index, 0, 1)) {
            Thread.onSpinWait();
        }
    }

    void unlock(int index) {
        VH_LOCK.setRelease(segment, 0L, index, 0);
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
}
