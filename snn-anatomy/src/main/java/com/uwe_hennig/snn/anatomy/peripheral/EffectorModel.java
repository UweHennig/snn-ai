/**
 * @(#)EffectorModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;
import static java.lang.foreign.MemoryLayout.PathElement.sequenceElement;
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
 * EffectorModel
 *
 * @author Uwe Hennig
 */
public class EffectorModel {
    public final Arena arena;
    public final int   capacity;
    public final int   rows;
    public final int   cols;

    MemorySegment segment;

    final VarHandle VH_LOCK;
    final VarHandle VH_TIME_WINDOw;
    final VarHandle VH_MATRIX;

    // ----- public -----

    // @formatter:off
    public EffectorModel(int numEffectors, int rows, int cols) {
        assert numEffectors > 0 : "invalid effector number";
        assert rows > 0 : "invalid rows in EffectorModel";
        assert cols >= 0 : "invalid columns in EffectorModel";

        this.arena = Arena.ofShared();
        this.capacity = numEffectors;
        this.rows = rows;
        this.cols = cols;

        GroupLayout LAYOUT = MemoryLayout
            .structLayout(
                JAVA_INT.withName("lock"),
                JAVA_FLOAT.withName("timeWindow"),
                MemoryLayout.sequenceLayout(rows, MemoryLayout.sequenceLayout(cols, JAVA_FLOAT)).withName("matrix"))
            .withByteAlignment(8);

        SequenceLayout poolLayout = MemoryLayout.sequenceLayout(numEffectors, LAYOUT);
        this.segment = arena.allocate(poolLayout);

        this.VH_LOCK = poolLayout.varHandle(sequenceElement(), MemoryLayout.PathElement.groupElement("lock"));
        this.VH_TIME_WINDOw = poolLayout.varHandle(sequenceElement(), groupElement("timeWindow"));

        this.VH_MATRIX = poolLayout.varHandle(sequenceElement(), // 1. Dimension: Effector with pool
            groupElement("matrix"), // 2. use matrix
            sequenceElement(), // 3. Dimension: rows
            sequenceElement() // 4. Dimension: columns
        );
    }
    // @formatter:on

    public void close() {
        if (arena != null) {
            arena.close();
        }
    }

    public int getCapacity() {
        return capacity;
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return cols;
    }

    // ----- getter/setter -----

    float getTimeWindow(int index) {
        return (float) VH_TIME_WINDOw.get(segment, 0L, (long) index);
    }

    void setTimeWindow(int index, float value) {
        VH_TIME_WINDOw.set(segment, 0L, (long) index, value);
    }

    public void setValue(int index, int row, int col, float value) {
        VH_MATRIX.set(segment, 0L, (long) index, (long) row, (long) col, value);
    }

    public float getValue(int index, int row, int col) {
        return (float) VH_MATRIX.get(segment, 0L, (long) index, (long) row, (long) col);
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
