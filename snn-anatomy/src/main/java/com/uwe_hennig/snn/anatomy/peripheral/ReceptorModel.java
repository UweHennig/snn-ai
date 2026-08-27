/**
 * @(#)ReceptorModel.java
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
import java.lang.foreign.MemoryLayout.PathElement;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.LockSupport;

/**
 * ReceptorModel
 *
 * @author Uwe Hennig
 */
public class ReceptorModel {
    public final Arena arena;
    public final int   capacity;

    public final int rows;
    public final int cols;

    MemorySegment segment;

    final VarHandle VH_LOCK;
    final VarHandle VH_INTAKE_DISTANCE;

    final VarHandle VH_TARGET_ID;
    final VarHandle VH_TARGET_TYPE;

    // ----- public -----

    // @formatter:off
    public ReceptorModel(int numReceptors, int rows, int cols) {
        assert numReceptors > 0 : "invalid receptor number";
        assert rows > 0  : "invalid rows in ReceptorModel";
        assert cols >= 0 : "invalid columns in ReceptorModel";

        this.arena = Arena.ofShared();
        this.capacity = numReceptors;
        this.rows = rows;
        this.cols = cols;

        GroupLayout TARGET = MemoryLayout.structLayout(
            JAVA_INT.withName("targetId"),
            JAVA_INT.withName("targetType")
        ).withByteAlignment(8);

        GroupLayout LAYOUT = MemoryLayout.structLayout(
            JAVA_INT.withName("lock"),
            JAVA_FLOAT.withName("intakeDistance"),
            MemoryLayout.sequenceLayout(rows, MemoryLayout.sequenceLayout(cols, TARGET)).withName("matrix")
        ).withByteAlignment(8);

        SequenceLayout poolLayout = MemoryLayout.sequenceLayout(numReceptors, LAYOUT);
        this.segment = arena.allocate(poolLayout);

        this.VH_LOCK = poolLayout.varHandle(sequenceElement(), MemoryLayout.PathElement.groupElement("lock"));
        this.VH_INTAKE_DISTANCE = poolLayout.varHandle(sequenceElement(), groupElement("intakeDistance"));
        this.VH_TARGET_ID = poolLayout.varHandle(
            PathElement.sequenceElement(), // Receptor-Index
            PathElement.groupElement("matrix"),
            PathElement.sequenceElement(), // Row-Index
            PathElement.sequenceElement(), // Col-Index
            PathElement.groupElement("targetId")
        );
        this.VH_TARGET_TYPE = poolLayout.varHandle(
            PathElement.sequenceElement(), // Receptor-Index
            PathElement.groupElement("matrix"),
            PathElement.sequenceElement(), // Row-Index
            PathElement.sequenceElement(), // Col-Index
            PathElement.groupElement("targetType")
        );
    }
    // @formatter:on

    public void close() {
        arena.close();
    }

    public int getCapacity() {
        return capacity;
    }

    // ----- getter/setter -----

    float getIntakeDistance(int index) {
        return (float) VH_INTAKE_DISTANCE.get(segment, 0L, (long) index);
    }

    void setIntakeDistance(int index, float value) {
        VH_INTAKE_DISTANCE.set(segment, 0L, (long) index, value);
    }

    int getTargetId(int index, int row, int col) {
        return (int) VH_TARGET_ID.get(segment, 0L, (long) index, row, col);
    }

    void setTargetId(int index, int row, int col, int id) {
        VH_TARGET_ID.set(segment, 0L, index, row, col, id);
    }

    int getTargetType(int index, int row, int col) {
        return (int) VH_TARGET_TYPE.get(segment, 0L, (long) index, row, col);
    }

    void setTargetType(int index, int row, int col, int value) {
        VH_TARGET_TYPE.set(segment, 0L, index, row, col, value);
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
