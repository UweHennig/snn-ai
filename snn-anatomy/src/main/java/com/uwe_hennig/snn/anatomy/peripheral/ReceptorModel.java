/**
 * @(#)ReceptorModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

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
    public final int rows;
    public final int cols;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    final VarHandle VH_TEMPORAL_FILTER_INDEX;
    final VarHandle VH_INFORMATION_FILTER_INDEX;
    final VarHandle VH_DENDRIT_MATRIX;
    final VarHandle VH_LOCK;

    final GroupLayout LAYOUT;

    // ----- public -----

    public ReceptorModel(int rows, int cols) {
        this.arena = Arena.ofShared();
        this.rows = rows;
        this.cols = cols;

        this.LAYOUT = MemoryLayout.structLayout(
            JAVA_INT.withName("lock"),
            JAVA_INT.withName("temporalFilterIndex"),
            JAVA_INT.withName("informationFilterIndex"),
            MemoryLayout.sequenceLayout(rows, MemoryLayout.sequenceLayout(cols, JAVA_INT)).withName("dendritMatrix")
        ).withByteAlignment(8);

        this.segment = arena.allocate(LAYOUT);

        this.VH_LOCK = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lock"));
        this.VH_TEMPORAL_FILTER_INDEX = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("temporalFilterIndex"));
        this.VH_INFORMATION_FILTER_INDEX = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("informationFilterIndex"));
        this.VH_DENDRIT_MATRIX = LAYOUT.varHandle(
            MemoryLayout.PathElement.groupElement("dendritMatrix"),
            MemoryLayout.PathElement.sequenceElement(), // Row
            MemoryLayout.PathElement.sequenceElement()  // Col
        );
    }

    public void close() {
        arena.close();
    }

    public int rows() {
        return rows;
    }

    public int columns() {
        return cols;
    }

    // ----- getter/setter -----

    int getTemporalFilterIndex() {
        return (int) VH_TEMPORAL_FILTER_INDEX.get(segment, 0L);
    }

    void setTemporalFilterIndex(int filterIndex) {
        VH_TEMPORAL_FILTER_INDEX.set(segment, 0L, filterIndex);
    }

    int getInformationFilterIndex() {
        return (int) VH_INFORMATION_FILTER_INDEX.get(segment, 0L);
    }

    void setInformationFilterIndex(int filterIndex) {
        VH_INFORMATION_FILTER_INDEX.set(segment, 0L, filterIndex);
    }

    void putDendritId(int row, int col, int dendritId) {
        VH_DENDRIT_MATRIX.set(segment, 0L, (long) row, (long) col, dendritId);
    }

    public int getDendritId(int row, int col) {
        return (int) VH_DENDRIT_MATRIX.get(segment, 0L, (long) row, (long) col);
    }

    public int[] getDendritIdRow(int row) {
        if (row < 0 || row >= rows) {
            throw new IndexOutOfBoundsException();
        }

        long offset = LAYOUT.byteOffset(
            MemoryLayout.PathElement.groupElement("dendritMatrix"),
            MemoryLayout.PathElement.sequenceElement(row)
        );

        long byteLength = cols * JAVA_INT.byteSize();

        return segment.asSlice(offset, byteLength).toArray(JAVA_INT);
    }

    public int[] getDendritIdCol(int col) {
        if (col < 0 || col >= cols) {
            throw new IndexOutOfBoundsException();
        }

        int[] result = new int[rows];
        for (int r = 0; r < rows; r++) {
            result[r] = (int) VH_DENDRIT_MATRIX.get(segment, 0L, (long) r, (long) col);
        }
        return result;
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
