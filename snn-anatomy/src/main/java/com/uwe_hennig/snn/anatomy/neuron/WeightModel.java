/**
 * @(#)WeightModel.java
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
 * WeightModel
 *
 * @author Uwe Hennig
 */
public final class WeightModel {
    final long  capacity;
    final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        JAVA_FLOAT.withName("weight"),
        JAVA_FLOAT.withName("preSynapticTime"),
        JAVA_FLOAT.withName("postSynapticTime"),
        JAVA_FLOAT.withName("hebbTimeRange"),
        JAVA_FLOAT.withName("weightScale"),
        JAVA_FLOAT.withName("hebbScale"),
        JAVA_FLOAT.withName("timeLimit")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK               = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));

    static final VarHandle VH_WEIGHT             = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("weight"));
    static final VarHandle VH_PRE_SYNAPTIC_TIME  = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("preSynapticTime"));
    static final VarHandle VH_POST_SYNAPTIC_TIME = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("postSynapticTime"));

    static final VarHandle VH_HEBB_TIME_RANGE = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("hebbTimeRange"));
    static final VarHandle VH_HEBB_SCALE      = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("hebbScale"));
    static final VarHandle VH_WEIGHT_SCALE    = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("weightScale"));
    static final VarHandle VH_TIME_LIMIT      = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("timeLimit"));
    // @formatter:on

    // ----- public -----

    public WeightModel(long capacity) {
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

    float getWeight(long index) {
        return (float) VH_WEIGHT.get(segment, 0L, index);
    }

    void setWeight(long index, float weight) {
        VH_WEIGHT.set(segment, 0L, index, weight);
    }

    float getPreSynapticTime(long index) {
        return (float) VH_PRE_SYNAPTIC_TIME.get(segment, 0L, index);
    }

    void setPreSynapticTime(long index, float timestamp) {
        VH_PRE_SYNAPTIC_TIME.set(segment, 0L, index, timestamp);
    }

    float getPostSynapticTime(long index) {
        return (float) VH_POST_SYNAPTIC_TIME.get(segment, 0L, index);
    }

    void setPostSynapticTime(long index, float timestamp) {
        VH_POST_SYNAPTIC_TIME.set(segment, 0L, index, timestamp);
    }

    float getHebbTimeRange(long index) {
        return (float) VH_HEBB_TIME_RANGE.get(segment, 0L, index);
    }

    void setHebbTimeRange(long index, float time) {
        VH_HEBB_TIME_RANGE.set(segment, 0L, index, time);
    }

    float getHebbScale(long index) {
        return (float) VH_HEBB_SCALE.get(segment, 0L, index);
    }

    void setHebbScale(long index, float timeRange) {
        VH_HEBB_SCALE.set(segment, 0L, index, timeRange);
    }

    float getWeightScale(long index) {
        return (float) VH_WEIGHT_SCALE.get(segment, 0L, index);
    }

    void setWeightScale(long index, float timeRange) {
        VH_WEIGHT_SCALE.set(segment, 0L, index, timeRange);
    }

    float getTimeLimit(long index) {
        return (float) VH_TIME_LIMIT.get(segment, 0L, index);
    }

    void setTimeLimit(long index, float timeRange) {
        VH_TIME_LIMIT.set(segment, 0L, index, timeRange);
    }

}
