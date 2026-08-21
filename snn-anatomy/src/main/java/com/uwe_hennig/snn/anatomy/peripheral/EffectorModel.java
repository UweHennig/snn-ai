/**
 * @(#)EffectorModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.LockSupport;

/**
 * EffectorModel
 *
 * @author Uwe Hennig
 */
public class EffectorModel {
    public final int   capacity;
    public final Arena arena;

    MemorySegment  segment;

    final GroupLayout LAYOUT;

    final VarHandle VH_LOCK;
    final VarHandle VH_TEMPORAL_FILTER_INDEX;
    final VarHandle VH_RELATED_ID_LIST;

    // ----- public -----

    // @formatter:off
    public EffectorModel(int capacity) {
        assert capacity > 0 : "invalid capacity";

        this.capacity = capacity;
        this.arena = Arena.ofShared();

        this.LAYOUT = MemoryLayout.structLayout(
            JAVA_INT.withName("lock"),
            JAVA_INT.withName("temporalFilterIndex"),
            MemoryLayout.sequenceLayout(capacity, JAVA_INT).withName("relatedIdList")
        ).withByteAlignment(8);

        this.VH_LOCK = LAYOUT.varHandle(
            MemoryLayout.PathElement.groupElement("lock")
        );

        this.VH_TEMPORAL_FILTER_INDEX = LAYOUT.varHandle(
            MemoryLayout.PathElement.groupElement("temporalFilterIndex")
        );

        this.VH_RELATED_ID_LIST = LAYOUT.varHandle(
            MemoryLayout.PathElement.groupElement("relatedIdList"),
            MemoryLayout.PathElement.sequenceElement()
        );

        this.segment = arena.allocate(LAYOUT);
    }
    // @formatter:on

    public void close() {
        arena.close();
    }

    public int getCapacity() {
        return capacity;
    }

    // ----- getter/setter -----

    int getTemporalFilterIndex() {
        return (int) VH_TEMPORAL_FILTER_INDEX.get(segment, 0L);
    }

    void setTemporalFilterIndex(int filterIndex) {
        VH_TEMPORAL_FILTER_INDEX.set(segment, 0L, filterIndex);
    }

    int getRelatedId(int index) {
        return (int) VH_RELATED_ID_LIST.get(segment, 0L, index);
    }

    void setRelatedId(int index, int identifier) {
        VH_RELATED_ID_LIST.set(segment, 0L, index, identifier);
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

            // no reader active and write flag is set or initial state.
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
}
