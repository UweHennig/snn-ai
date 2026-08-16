/**
 * @(#)FilterModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.LockSupport;

/**
 * FilterModel
 *
 * @author Uwe Hennig
 */
public class FilterModel {
    public final int   capacity;
    public final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        MemoryLayout.paddingLayout(4),
        JAVA_FLOAT.withName("minValue"),
        JAVA_FLOAT.withName("maxValue")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));

    static final VarHandle VH_MIN_VALUE =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("minValue"));

    static final VarHandle VH_MAX_VALUE =
        LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("maxValue"));
    // @formatter:on

    // ----- public -----

    public FilterModel(int capacity) {
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

    int getMinValue(int index) {
        return (int) VH_MIN_VALUE.get(segment, 0L, index);
    }

    void setMinValue(int index, float minValue) {
        VH_MIN_VALUE.set(segment, 0L, index, minValue);
    }

    int getMaxValue(int index) {
        return (int) VH_MAX_VALUE.get(segment, 0L, index);
    }

    void setMaxValue(int index, float maxValue) {
        VH_MAX_VALUE.set(segment, 0L, index, maxValue);
    }
}
