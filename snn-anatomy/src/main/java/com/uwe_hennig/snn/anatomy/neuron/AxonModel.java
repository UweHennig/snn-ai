/**
 * @(#)AxonModel.java
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
import java.util.concurrent.locks.LockSupport;

/**
 * AxonModel
 * @author Uwe Hennig
 */
public final class AxonModel {
    public final int   capacity;
    public final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        MemoryLayout.paddingLayout(4),
        JAVA_INT.withName("fiedlId"),
        JAVA_INT.withName("neuronId"),
        JAVA_INT.withName("synapsesRef"),
        JAVA_INT.withName("modulatorId")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));
    static final VarHandle VH_FIELD_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fiedlId"));
    static final VarHandle VH_NEURON_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("neuronId"));
    static final VarHandle VH_SYNAPSES_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("synapsesRef"));
    static final VarHandle VH_MODULATOR_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("modulatorId"));
    // @formatter:off

    // ----- public -----

    public AxonModel(int capacity) {
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

    int getFiedlId(int index) {
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

    int getModulatorId(int index) {
        return (int) VH_MODULATOR_ID.get(segment, 0L, index);
    }

    void setModulatorId(int index, int value) {
        VH_MODULATOR_ID.set(segment, 0L, index, value);
    }

    int getSynapseRef(int index) {
        return (int) VH_SYNAPSES_REF.get(segment, 0L, index);
    }

    void setSynapseRef(int index, int value) {
        VH_SYNAPSES_REF.set(segment, 0L, index, value);
    }

}
