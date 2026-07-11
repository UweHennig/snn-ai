/**
 * @(#)NeuronModel.java
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
 * NeuronModel
 *
 * @author Uwe Hennig
 */
public final class NeuronModel {
    final int   capacity;
    final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        JAVA_INT.withName("fiedlId"),
        JAVA_INT.withName("dendritRef"),
        JAVA_INT.withName("somaId"),
        JAVA_INT.withName("axonId"),
        JAVA_INT.withName("synapseRef")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));

    static final VarHandle VH_FIELD_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fiedlId"));

    static final VarHandle VH_DENDRIT_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("dendritRef"));
    static final VarHandle VH_SOMA_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("somaId"));
    static final VarHandle VH_AXON_ID =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("axonId"));
    static final VarHandle VH_SYNAPSE_REF =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("synapseRef"));
    // @formatter:on

    // ----- public -----
    public NeuronModel(int capacity) {
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

    private static final int WRITER_WAITING = 0x40000000; // Bit 30
    private static final int WRITER_ACTIVE  = 0xFFFFFFFF; // -1

    void writeLock(int index) {
        int spins = 0;
        // set the WRITER_WAITING flag to indicate a write request
        while (true) {
            int current = (int) VH_LOCK.getVolatile(segment, 0L, index);
            if ((current & WRITER_WAITING) == 0) {
                if (VH_LOCK.compareAndSet(segment, 0L, index, current, current | WRITER_WAITING)) {
                    break;
                }
            } else {
                break;
            }
            backoff(spins++);
        }

        // set the lock
        spins = 0;
        while (true) {
            int current = (int) VH_LOCK.getVolatile(segment, 0L, index);

            // no reader aktive and write flag is set or initial state.
            if (current == WRITER_WAITING || current == 0) {
                if (VH_LOCK.compareAndSet(segment, 0L, index, current, WRITER_ACTIVE)) {
                    return;
                }
            }

            backoff(spins++);
        }
    }

    void writeUnlock(int index) {
        VH_LOCK.setRelease(segment, 0L, index, 0);
    }

    boolean readLock(int index) {
        int spins = 0;
        while (true) {
            int current = (int) VH_LOCK.getVolatile(segment, 0L, index);

            // give priority to the writers
            if (current < 0 || (current & WRITER_WAITING) != 0) {
                backoff(spins++);
                continue;
            }

            // increment reader counter
            if (VH_LOCK.compareAndSet(segment, 0L, index, current, current + 1)) {
                return true;
            }
        }
    }

    void readUnlock(int index) {
       // decrement reader counter
       VH_LOCK.getAndAdd(segment, 0L, index, -1);
    }

    void backoff(int spins) {
        if (spins < 64) {
            Thread.onSpinWait();
        } else {
            LockSupport.parkNanos(1);
        }
    }


    // ----- getter/setter -----

    int getFiedlId(int index) {
        return (int) VH_FIELD_ID.get(segment, 0L, index);
    }

    void setFieldId(int index, int value) {
        VH_FIELD_ID.set(segment, 0L, index, value);
    }

    void setRef(int index, int dendritRef, int somaId, int axonId, int synapseRef) {
        VH_DENDRIT_REF.set(segment, 0L, index, dendritRef);
        VH_SOMA_ID.set(segment, 0L, index, somaId);
        VH_AXON_ID.set(segment, 0L, index, axonId);
        VH_SYNAPSE_REF.set(segment, 0L, index, synapseRef);
    }

    int getDendritRef(int index) {
        return (int) VH_DENDRIT_REF.get(segment, 0L, index);
    }

    int getSomaId(int index) {
        return (int) VH_SOMA_ID.get(segment, 0L, index);
    }

    int getAxonId(int index) {
        return (int) VH_AXON_ID.get(segment, 0L, index);
    }

    int getSynapseRef(int index) {
        return (int) VH_SYNAPSE_REF.get(segment, 0L, index);
    }
}
