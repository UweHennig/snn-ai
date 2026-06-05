/**
 * @(#)DendritModel.java
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
 * DendritModel
 * @author Uwe Hennig
 */
public final class DendritModel {
    public final long  capacity;
    public final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        MemoryLayout.paddingLayout(4),
        // structure
        JAVA_LONG.withName("fiedlId"),
        JAVA_LONG.withName("neuronId"),
        // data
        JAVA_LONG.withName("weightId"),
        // reference
        JAVA_LONG.withName("somaId")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));
    static final VarHandle VH_FIELD_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fiedlId"));
    static final VarHandle VH_NEURON_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("neuronId"));
    static final VarHandle VH_WEIGHT_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("weightId"));
    static final VarHandle VH_SOMA_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("somaId"));
    // @formatter:on

    // ----- public -----

    public DendritModel(long capacity) {
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

    long getFiedlId(long index) {
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

    long getSomaId(long index) {
        return (long) VH_SOMA_ID.get(segment, 0L, index);
    }

    void setSomaId(long index, long value) {
        VH_SOMA_ID.set(segment, 0L, index, value);
    }

    long getWeighId(long index) {
        return (long) VH_WEIGHT_ID.get(segment, 0L, index);
    }

    void setWeightId(long index, long value) {
        VH_WEIGHT_ID.set(segment, 0L, index, value);
    }
}
