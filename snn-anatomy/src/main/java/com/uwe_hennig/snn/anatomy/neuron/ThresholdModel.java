/**
 * @(#)ThresholdModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.invoke.VarHandle;

/**
 * ThresholdModel
 *
 * @author Uwe Hennig
 */
public final class ThresholdModel {
    final int   capacity;
    final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        JAVA_FLOAT.withName("threshold"),
        JAVA_FLOAT.withName("thresholdScale"),
        JAVA_FLOAT.withName("timeLimit")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("lock"));
    static final VarHandle VH_THRESHOLD = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("threshold"));
    static final VarHandle VH_THRESHOLD_SCALE = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("thresholdScale"));
    static final VarHandle VH_TIME_LIMIT = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("timeLimit"));
    // @formatter:on

    // ----- public -----

    public ThresholdModel(int capacity) {
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

    float getThreshold(int index) {
        return (float) VH_THRESHOLD.get(segment, 0L, index);
    }

    void setThreshold(int index, float value) {
        VH_THRESHOLD.set(segment, 0L, index, value);
    }

    float getThresholdScale(int index) {
        return (float) VH_THRESHOLD_SCALE.get(segment, 0L, index);
    }

    void setThresholdScale(int index, float value) {
        VH_THRESHOLD_SCALE.set(segment, 0L, index, value);
    }

    float getTimeLimit(int index) {
        return (float) VH_TIME_LIMIT.get(segment, 0L, index);
    }

    void setTimeLimit(int index, float timeRange) {
        VH_TIME_LIMIT.set(segment, 0L, index, timeRange);
    }
}
