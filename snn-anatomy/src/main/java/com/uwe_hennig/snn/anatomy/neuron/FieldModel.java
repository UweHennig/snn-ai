/**
 * @(#)FieldModel.java
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
import java.util.concurrent.locks.LockSupport;

/**
 * FieldModel
 * TODO Ref oder nicht. Rolle von Id und Ref.
 * @author Uwe Hennig
 */
public final class FieldModel {
    public final int   capacity;
    public final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        JAVA_INT.withName("type"),
        JAVA_INT.withName("level"),
        JAVA_INT.withName("fieldId"),
        JAVA_LONG.withName("neuronRef"),
        JAVA_LONG.withName("outNeighborsRef"),
        JAVA_LONG.withName("inNeighborsRef")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));

    static final VarHandle VH_TYPE =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("type"));
    static final VarHandle VH_LEVEL =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("level"));

    static final VarHandle VH_FIELD_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fieldId"));
    static final VarHandle VH_NEURON_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("neuronRef"));
    static final VarHandle VH_OUT_NEIGHBORS_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("outNeighborsRef"));
    static final VarHandle VH_IN_NEIGHBORS_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("inNeighborsRef"));
    // @formatter:on

    // ----- public -----

    public FieldModel(int capacity) {
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

    void writeLock(int index) {
        int spins = 0;
        while (!VH_LOCK.compareAndSet(segment, 0L, index, 0, -1)) {
            if (spins < 64) {
                Thread.onSpinWait();
                spins++;
            } else {
                LockSupport.parkNanos(1);
            }
        }
    }

    void writeUnlock(int index) {
        VH_LOCK.setRelease(segment, 0L, index, 0);
    }

    // ----- getter/setter -----

    int getType(int index) {
        return (int) VH_TYPE.get(segment, 0L, index);
    }

    void setType(int index, int value) {
        VH_TYPE.set(segment, 0L, index, value);
    }

    int getLevel(int index) {
        return (int) VH_LEVEL.get(segment, 0L, index);
    }

    void setLevel(int index, int value) {
        VH_LEVEL.set(segment, 0L, index, value);
    }

    long getNeuronRef(int index) {
        return (long)VH_NEURON_REF.get(segment, 0L, index);
    }

    void setNeuronRef(int index, long value) {
        VH_NEURON_REF.set(segment, 0L, index, value);
    }

    long getOutNeighborsRef(int index) {
        return (long)VH_OUT_NEIGHBORS_REF.get(segment, 0L, index);
    }

    void setOutNeighborsRef(int index, long value) {
        VH_OUT_NEIGHBORS_REF.set(segment, 0L, index, value);
    }

    long getInNeighborsRef(int index) {
        return (long)VH_IN_NEIGHBORS_REF.get(segment, 0L, index);
    }

    void setInNeighborsRef(int index, long value) {
        VH_IN_NEIGHBORS_REF.set(segment, 0L, index, value);
    }

    int getFieldId(int index) {
        return (int)VH_FIELD_ID.get(segment, 0L, index);
    }

    void setFieldId(int index, long value) {
        VH_FIELD_ID.set(segment, 0L, index, value);
    }
}
