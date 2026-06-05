/**
 * @(#)PlasticityModel.java
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
 * PlasticityModel
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class PlasticityModel {
    final long  capacity;
    final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock")
    );

    static final VarHandle VH_LOCK = LAYOUT.arrayElementVarHandle(
        MemoryLayout.PathElement.groupElement("lock"));

    // @formatter:on

    public PlasticityModel(long capacity) {
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

}
