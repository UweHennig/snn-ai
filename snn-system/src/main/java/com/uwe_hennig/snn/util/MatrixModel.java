/**
 * @(#)MatrixModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * MatrixModel
 * @formatter:off
+------------------------ HEADER-MATRIX -------------------------+
| +----------------------- META -------------------------------+ |
| | capacity                                                   | |
| | ...                                                        | |
| +------------------------------------------------------------+ |
| +---------------------- HEADER 0 (fixed) --------------------+ |
| | 0x00: header data 1                                        | |
| | 0x04: header data 2                                        | |
| | 0x08: header data 3                                        | |
| | ...                                                        | |
| +------------------------------------------------------------+ |
| +-------------------------- MATRIX 0 ------------------------+ |
| | +---------------------- Row 0 ---------------------------+ | |
| | | CELL (0,0) | CELL (0,1) | CELL (0,2) | ...             | | |
| | +---------------------- Row 1 ---------------------------+ | |
| | | CELL (1,0) | CELL (1,1) | CELL (1,2) | ...             | | |
| | +---------------------- Row 2 ---------------------------+ | |
| | | CELL (2,0) | CELL (2,1) | CELL (2,2) | ...             | | |
| | ...                                                      | | |
| +----------------------------------------------------------+ | |
| +---------------------- HEADER 1 (fixed) --------------------+ |
| | 0x00: header data 1                                        | |
| | 0x04: header data 2                                        | |
| | 0x08: header data 3                                        | |
| | ...                                                        | |
| +------------------------------------------------------------+ |
| +-------------------------- MATRIX 1 ------------------------+ |
| | +---------------------- Row 0 ---------------------------+ | |
| | | CELL (0,0) | CELL (0,1) | CELL (0,2) | ...             | | |
| | +---------------------- Row 1 ---------------------------+ | |
| | | CELL (1,0) | CELL (1,1) | CELL (1,2) | ...             | | |
| | +---------------------- Row 2 ---------------------------+ | |
| | | CELL (2,0) | CELL (2,1) | CELL (2,2) | ...             | | |
| | ...                                                      | | |
| +----------------------------------------------------------+ | |
+----------------------------------------------------------------+
 * @author Uwe Hennig
 */
public final class MatrixModel {
    private static final long DATA_SIZE = 8;
    private static final long META_SIZE = 24;           // Lokales Matrix-Meta
    private static final long GLOBAL_HEADER_SIZE = 24;  // Globaler Root-Header

    private final int capacity;
    private final Arena arena;
    private final MemorySegment segment;

    private int lastMatrixIndex = 0;
    private long nextFreeByte;

    static final VarHandle VH_STATUS = ValueLayout.JAVA_INT.varHandle();

    public MatrixModel(int capacity, long totalSize) {
        this.capacity = capacity;
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(totalSize);

        segment.set(ValueLayout.JAVA_INT, 0, capacity);
        segment.set(ValueLayout.JAVA_INT, 20, (int) totalSize);

        this.nextFreeByte = GLOBAL_HEADER_SIZE + (capacity * 8L);
    }

    /**
     * Registers a matrix and returns the logical index (0...capacity-1).
     */
    public int registerMatrix(int numHeaders, int numRows, int numColumns, int numSlotsPerCell) {
        if (lastMatrixIndex >= this.capacity) {
            throw new RuntimeException("Capacity exceeded");
        }

        int index = lastMatrixIndex;
        long matrixStartOffset = nextFreeByte;

        writeLocalMeta(matrixStartOffset, numHeaders, numRows, numColumns, numSlotsPerCell);

        writeMatrixOffset(index, matrixStartOffset);

        long hSize = (numHeaders + 1L) * DATA_SIZE;

        long dSize = (long) numRows * (long) numColumns * numSlotsPerCell * DATA_SIZE;

        this.nextFreeByte += (META_SIZE + hSize + dSize);

        lastMatrixIndex++;
        return index;
    }

    public static long matrixSize(int numHeaders, int numRows, int numColumns, int numSlotsPerCell) {
        long hSize = (numHeaders + 1L) * DATA_SIZE;
        long dSize = (long) numRows * (long) numColumns * numSlotsPerCell * DATA_SIZE;
        return hSize + dSize;
    }

    public static long metaSize() {
        return META_SIZE;
    }

    private void writeMatrixOffset(int index, long physicalOffset) {
        segment.set(ValueLayout.JAVA_LONG, GLOBAL_HEADER_SIZE + (index * 8L), physicalOffset);
    }

    private void writeLocalMeta(long physicalOffset, int nH, int nR, int nC, int nS) {
        segment.set(ValueLayout.JAVA_INT, physicalOffset,      this.capacity);
        segment.set(ValueLayout.JAVA_INT, physicalOffset + 4,  nH);
        segment.set(ValueLayout.JAVA_INT, physicalOffset + 8,  nR);
        segment.set(ValueLayout.JAVA_INT, physicalOffset + 12, nC);
        segment.set(ValueLayout.JAVA_INT, physicalOffset + 16, nS);
        segment.set(ValueLayout.JAVA_INT, physicalOffset + 20, (int) segment.byteSize());
    }

    // ------------------------------------------------------------
    // INTERNAL LOGIC (Mapping of logical index m to physical offset)
    // ------------------------------------------------------------

    private long getMatrixPhysicalStart(int m) {
        return segment.get(ValueLayout.JAVA_LONG, GLOBAL_HEADER_SIZE + (m * 8L));
    }

    private long headerOffset(int m, long h) {
        return getMatrixPhysicalStart(m) + META_SIZE + (h * DATA_SIZE);
    }

    private long cellOffset(int m, long r, long c, long n) {
        long start = getMatrixPhysicalStart(m);

        int nH = segment.get(ValueLayout.JAVA_INT, start + 4);
        int nC = segment.get(ValueLayout.JAVA_INT, start + 12);
        int nS = segment.get(ValueLayout.JAVA_INT, start + 16);

        long hSection = (nH + 1L) * DATA_SIZE;
        long rSize = (long) nC * (long) nS * DATA_SIZE;
        long cSize = nS * DATA_SIZE;

        return start + META_SIZE + hSection + (r * rSize) + (c * cSize) + (n * DATA_SIZE);
    }

    // ------------------------------------------------------------
    // PUBLIC API (Always via logical index m)
    // ------------------------------------------------------------

    /**
     * Returns the number of headers for the matrix at the logical index m.
     */
    public int getNumHeaders(int m) {
        long start = getMatrixPhysicalStart(m);
        return segment.get(ValueLayout.JAVA_INT, start + 4);
    }

    /**
     * Returns the number of rows in the matrix at the logical index m.
     */
    public int getNumRows(int m) {
        long start = getMatrixPhysicalStart(m);
        return segment.get(ValueLayout.JAVA_INT, start + 8);
    }

    /**
     * Returns the number of columns in the matrix at the logical index m.
     */
    public int getNumColumns(int m) {
        long start = getMatrixPhysicalStart(m);
        return segment.get(ValueLayout.JAVA_INT, start + 12);
    }

    /**
     * Returns the number of slots per cell for the matrix at the logical index m.
     */
    public int getNumSlotsPerCell(int m) {
        long start = getMatrixPhysicalStart(m);
        return segment.get(ValueLayout.JAVA_INT, start + 16);
    }

    public void setHeaderInt(int m, long h, int val) {
        segment.set(ValueLayout.JAVA_INT, headerOffset(m, h), val);
    }

    public int getHeaderInt(int m, long h) {
        return segment.get(ValueLayout.JAVA_INT, headerOffset(m, h));
    }

    public void setHeaderFloat(int m, long h, float val) {
        segment.set(ValueLayout.JAVA_FLOAT, headerOffset(m, h), val);
    }

    public float getHeaderFloat(int m, long h) {
        return segment.get(ValueLayout.JAVA_FLOAT, headerOffset(m, h));
    }

    public void setHeaderDouble(int m, long h, double val) {
        segment.set(ValueLayout.JAVA_DOUBLE, headerOffset(m, h), val);
    }

    public double getHeaderDouble(int m, long h) {
        return segment.get(ValueLayout.JAVA_DOUBLE, headerOffset(m, h));
    }

    public void setHeaderLong(int m, long h, long val) {
        segment.set(ValueLayout.JAVA_LONG, headerOffset(m, h), val);
    }

    public long getHeaderLong(int m, long h) {
        return segment.get(ValueLayout.JAVA_LONG, headerOffset(m, h));
    }

    public void setCellInt(int m, long r, long c, long n, int val) {
        segment.set(ValueLayout.JAVA_INT, cellOffset(m, r, c, n), val);
    }

    public int getCellInt(int m, long r, long c, long n) {
        return segment.get(ValueLayout.JAVA_INT, cellOffset(m, r, c, n));
    }

    public void setCellDouble(int m, long r, long c, long n, double val) {
        segment.set(ValueLayout.JAVA_DOUBLE, cellOffset(m, r, c, n), val);
    }

    public double getCellDouble(int m, long r, long c, long n) {
        return segment.get(ValueLayout.JAVA_DOUBLE, cellOffset(m, r, c, n));
    }

    public boolean setStatus(int m, int currentStatus, int newStatus) {
        long start = getMatrixPhysicalStart(m);
        int nH = segment.get(ValueLayout.JAVA_INT, start + 4);
        long offset = headerOffset(m, nH);

        while (!VH_STATUS.compareAndSet(segment, offset, currentStatus, newStatus)) {
            Thread.onSpinWait();
        }
        return true;
    }

    public void releaseStatus(int m) {
        long start = getMatrixPhysicalStart(m);
        int nH = segment.get(ValueLayout.JAVA_INT, start + 4);
        long offset = headerOffset(m, nH);

        segment.set(ValueLayout.JAVA_INT, offset, 0);
    }

    public int getStatus(int m) {
        long start = getMatrixPhysicalStart(m);
        int nH = segment.get(ValueLayout.JAVA_INT, start + 4);
        return segment.get(ValueLayout.JAVA_INT, headerOffset(m, nH));
    }

    public int getCapacity() {
        return segment.get(ValueLayout.JAVA_INT, 0);
    }

    public int getByteSize() {
        return segment.get(ValueLayout.JAVA_INT, 20);
    }

    public void close() {
        if (arena != null) {
            arena.close();
        }
    }
}
