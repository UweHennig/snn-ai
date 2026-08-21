/**
 * @(#)ReceptorModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import static java.lang.foreign.MemoryLayout.PathElement.groupElement;
import static java.lang.foreign.MemoryLayout.PathElement.sequenceElement;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
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
    public final int   rows;
    public final int   cols;

    MemorySegment segment;

    final VarHandle VH_TEMPORAL_FILTER_INDEX;
    final VarHandle VH_INFORMATION_FILTER_INDEX;
    final VarHandle VH_DENDRIT_MATRIX;
    final VarHandle VH_LOCK;

    // ----- public -----

    public ReceptorModel(int numReceptors, int rows, int cols) {
        this.arena = Arena.ofShared();
        this.rows = rows;
        this.cols = cols;

        GroupLayout LAYOUT = MemoryLayout.structLayout(
                JAVA_INT.withName("lock"),
                MemoryLayout.paddingLayout(4),
                JAVA_INT.withName("temporalFilterIndex"),
                JAVA_INT.withName("informationFilterIndex"),
                MemoryLayout.sequenceLayout(rows, MemoryLayout.sequenceLayout(cols, JAVA_INT)).withName("dendritMatrix"))
            .withByteAlignment(8);

        SequenceLayout poolLayout = MemoryLayout.sequenceLayout(numReceptors, LAYOUT);
        this.segment = arena.allocate(poolLayout);

        this.VH_LOCK = poolLayout.varHandle(sequenceElement(), MemoryLayout.PathElement.groupElement("lock"));
        this.VH_TEMPORAL_FILTER_INDEX = poolLayout.varHandle(sequenceElement(), groupElement("temporalFilterIndex"));
        this.VH_INFORMATION_FILTER_INDEX = poolLayout.varHandle(sequenceElement(), groupElement("informationFilterIndex"));

        this.VH_DENDRIT_MATRIX = poolLayout.varHandle(
            sequenceElement(),             // 1. Dimension: Receptor with pool
            groupElement("dendritMatrix"), // 2. use matrix
            sequenceElement(),             // 3. Dimension: rows
            sequenceElement()              // 4. Dimension: columns
        );
    }

    public void close() {
        if (arena != null) {
            arena.close();
        }
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return cols;
    }

    // ----- getter/setter -----

    int getTemporalFilterIndex(int index) {
        return (int) VH_TEMPORAL_FILTER_INDEX.get(segment, 0L, (long) index);
    }

    void setTemporalFilterIndex(int index, int value) {
        VH_TEMPORAL_FILTER_INDEX.set(segment, 0L, (long) index, value);
    }

    int getInformationFilterIndex(int index) {
        return (int) VH_INFORMATION_FILTER_INDEX.get(segment, 0L, (long) index);
    }

    void setInformationFilterIndex(int index, int value) {
        VH_INFORMATION_FILTER_INDEX.set(segment, 0L, (long) index, value);
    }

    public void setDendriteId(int index, int row, int col, int dendriteId) {
        VH_DENDRIT_MATRIX.set(segment, 0L, (long) index, (long) row, (long) col, dendriteId);
    }

    public int getDendriteId(int index, int row, int col) {
        return (int) VH_DENDRIT_MATRIX.get(segment, 0L, (long) index, (long) row, (long) col);
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
