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

/**
 * StimulusModel
 * TODO trgIndex is an index in MulitList
 *
 * @author Uwe Hennig
 */
public class StimulusModel {
    final int   capacity;
    final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    public static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),      // 0: free, 1: occupied
        MemoryLayout.paddingLayout(4),
        JAVA_INT.withName("srcIndex"),
        JAVA_INT.withName("trgIndex"),
        JAVA_LONG.withName("trgRef"),
        JAVA_LONG.withName("expiry"),
        JAVA_INT.withName("type"),
        JAVA_FLOAT.withName("value")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK      = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));
    static final VarHandle VH_EXPIRY    = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("expiry"));
    static final VarHandle VH_SRC_INDEX = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("srcIndex"));
    static final VarHandle VH_TRG_INDEX = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("trgIndex"));
    static final VarHandle VH_TRG_REF   = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("trgRef"));
    static final VarHandle VH_TYPE      = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("type"));
    static final VarHandle VH_VALUE     = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("value"));
    // @formatter:on

    public StimulusModel(int capacity) {
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

    public boolean tryLock(long index) {
        return (int) VH_LOCK.compareAndExchange(segment, 0L, index, 0, 1) == 0;
    }

    public boolean isLocked(long index) {
        return (int) VH_LOCK.get(segment, 0L, index) == 1;
    }

    // ----- getter/setter -----

    long getExpiry(int index) {
        return (long) VH_EXPIRY.get(segment, 0L, index);
    }

    void setExpiry(int index, long value) {
        VH_EXPIRY.set(segment, 0L, index, value);
    }

    int getSrc(int index) {
        return (int) VH_SRC_INDEX.get(segment, 0L, index);
    }

    void setSrc(int index, int value) {
        VH_SRC_INDEX.set(segment, 0L, index, value);
    }

    int getTrg(int index) {
        return (int) VH_TRG_INDEX.get(segment, 0L, index);
    }

    void setTrg(int index, int value) {
        VH_TRG_INDEX.set(segment, 0L, index, value);
    }

    long getTrgRef(int index) {
        return (long) VH_TRG_REF.get(segment, 0L, index);
    }

    void setTrgRef(int index, int value) {
        VH_TRG_REF.set(segment, 0L, index, value);
    }

    int getTrgType(int index) {
        return (int) VH_TYPE.get(segment, 0L, index);
    }

    void setTrgType(int index, int type) {
        VH_TYPE.set(segment, 0L, index, type);
    }

    float getValue(int index) {
        return (float) VH_VALUE.get(segment, 0L, index);
    }

    void setValue(int index, float value) {
        VH_VALUE.set(segment, 0L, index, value);
    }

}
