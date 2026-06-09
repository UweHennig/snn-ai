/**
 * @(#)FieldModel.java
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
 * FieldModel
 *
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
        JAVA_INT.withName("parentsRef"),
        JAVA_INT.withName("childrenRef"),
        JAVA_INT.withName("neuronsRef")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));
    static final VarHandle VH_TYPE =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("type"));
    static final VarHandle VH_LEVEL =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("level"));
    static final VarHandle VH_PARENTS_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("parentsRef"));
    static final VarHandle VH_CHILDREN_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("childrenRef"));
    static final VarHandle VH_NEURONS_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("neuronsRef"));
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

    int getParentsRef(int index) {
        return (int) VH_PARENTS_REF.get(segment, 0L, index);
    }

    void setParentsRef(int index, int value) {
        VH_PARENTS_REF.set(segment, 0L, index, value);
    }

    int getChildrenRef(int index) {
        return (int) VH_CHILDREN_REF.get(segment, 0L, index);
    }

    void setChildrenRef(int index, int value) {
        VH_CHILDREN_REF.set(segment, 0L, index, value);
    }

    int getNeuronsRef(int index) {
        return (int) VH_NEURONS_REF.get(segment, 0L, index);
    }

    void setNeuronsRef(int index, int value) {
        VH_NEURONS_REF.set(segment, 0L, index, value);
    }
}
