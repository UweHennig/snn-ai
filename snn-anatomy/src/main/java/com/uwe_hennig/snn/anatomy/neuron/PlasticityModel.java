/**
 * @(#)PlasticityModel.java
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
 * PlasticityModel
 *
 * @author Uwe Hennig
 */
public final class PlasticityModel {
    final int   capacity;
    final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        MemoryLayout.paddingLayout(4),

        // === DYNAMIC STATE ===
        JAVA_FLOAT.withName("currentPotential"),
        JAVA_FLOAT.withName("lastUpdateTime"),

        // === KNOWLEDGE STATE ===
        JAVA_FLOAT.withName("targetPotential"),
        JAVA_FLOAT.withName("restingPotential"),

        // === TIME CONSTANTS ===
        JAVA_FLOAT.withName("targetTime"),
        JAVA_FLOAT.withName("restingTime"),

        // === RATES ===
        JAVA_FLOAT.withName("targetRate"),
        JAVA_FLOAT.withName("restingRate")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));

    static final VarHandle VZ_CURRENT_POTENTIAL = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("currentPotential"));
    static final VarHandle VZ_LAST_UPDATE_TIME  = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lastUpdateTime"));
    static final VarHandle VZ_TARGET_POTENTIAL  = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("targetPotential"));
    static final VarHandle VZ_RESTING_POTENTIAL = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("restingPotential"));
    static final VarHandle VZ_TARGET_TIME       = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("targetTime"));
    static final VarHandle VZ_RESTING_TIME      = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("restingTime"));
    static final VarHandle VZ_TARGET_RATE       = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("targetRate"));
    static final VarHandle VZ_RESTING_RATE      = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("restingRate"));
    // @formatter:on

    public PlasticityModel(int capacity) {
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

    float getCurrentPotential(int index) {
        return (float) VZ_CURRENT_POTENTIAL.get(segment, 0L, index);
    }

    void setCurrentPotential(int index, float value) {
        VZ_CURRENT_POTENTIAL.set(segment, 0L, index, value);
    }

    float getLastUpdateTime(int index) {
        return (float) VZ_LAST_UPDATE_TIME.get(segment, 0L, index);
    }

    void setLastUpdateTime(int index, float value) {
        VZ_LAST_UPDATE_TIME.set(segment, 0L, index, value);
    }

    float getTargetPotential(int index) {
        return (float) VZ_TARGET_POTENTIAL.get(segment, 0L, index);
    }

    void setTargetPotential(int index, float value) {
        VZ_TARGET_POTENTIAL.set(segment, 0L, index, value);
    }

    float getRestingPotential(int index) {
        return (float) VZ_RESTING_POTENTIAL.get(segment, 0L, index);
    }

    void setRestingPotential(int index, float value) {
        VZ_RESTING_POTENTIAL.set(segment, 0L, index, value);
    }

    float getTargetTime(int index) {
        return (float) VZ_TARGET_TIME.get(segment, 0L, index);
    }

    void setTargetTime(int index, float value) {
        VZ_TARGET_TIME.set(segment, 0L, index, value);
    }

    float getRestingTime(int index) {
        return (float) VZ_RESTING_TIME.get(segment, 0L, index);
    }

    void setRestingTime(int index, float value) {
        VZ_RESTING_TIME.set(segment, 0L, index, value);
    }

    float getTargetRate(int index) {
        return (float) VZ_TARGET_RATE.get(segment, 0L, index);
    }

    void setTargetRate(int index, float value) {
        VZ_TARGET_RATE.set(segment, 0L, index, value);
    }

    float getRestingRate(int index) {
        return (float) VZ_RESTING_RATE.get(segment, 0L, index);
    }

    void setRestingRate(int index, float value) {
        VZ_RESTING_RATE.set(segment, 0L, index, value);
    }

}
