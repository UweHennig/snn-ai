/**
 * @(#)ModulatorModel.java
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
 * ModulatorModel
 *
 * @author Uwe Hennig
 */
public final class ModulatorModel {
    final int   capacity;
    final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        MemoryLayout.paddingLayout(4),
        JAVA_FLOAT.withName("modulationGain"),
        JAVA_FLOAT.withName("gainDuration"),
        JAVA_FLOAT.withName("modulationGainDefault"),
        JAVA_FLOAT.withName("lastEventTime")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("lock"));
    static final VarHandle VH_MODULATION_GAIN = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("modulationGain"));
    static final VarHandle VH_GAIN_DURATION = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("gainDuration"));
    static final VarHandle VH_MODULATION_GAIN_DEFAULT = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("modulationGainDefault"));
    static final VarHandle VH_LAST_EVENT_TIME = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("lastEventTime"));
    // @formatter:on

    // ----- public -----

    public ModulatorModel(int capacity) {
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

    float getModulationGain(int index) {
        return (float) VH_MODULATION_GAIN.get(segment, 0L, index);
    }

    void setModulationGain(int index, float value) {
        VH_MODULATION_GAIN.set(segment, 0L, index, value);
    }

    float getGainDuration(int index) {
        return (float) VH_GAIN_DURATION.get(segment, 0L, index);
    }

    void setGainDuration(int index, float value) {
        VH_GAIN_DURATION.set(segment, 0L, index, value);
    }

    float getModulationGainDefault(int index) {
        return (float) VH_MODULATION_GAIN_DEFAULT.get(segment, 0L, index);
    }

    void setModulationGainDefault(int index, float value) {
        VH_MODULATION_GAIN_DEFAULT.set(segment, 0L, index, value);
    }

    float getLastEventTime(int index) {
        return (float) VH_LAST_EVENT_TIME.get(segment, 0L, index);
    }

    void setLastEventTime(int index, float value) {
        VH_LAST_EVENT_TIME.set(segment, 0L, index, value);
    }
}
