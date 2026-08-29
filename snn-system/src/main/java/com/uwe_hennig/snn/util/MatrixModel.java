/**
 * @(#)Matrix.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * Matrix
 * @formatter:off
+------------------------ HEADER-MATRIX -------------------------+
| +---------------------- HEADER (fixed) ----------------------+ |
| | 0x00: header data 1                                        | |
| | 0x04: header data 2                                        | |
| | 0x08: header data 3                                        | |
| | ...                                                        | |
| +------------------------------------------------------------+ |
| +-------------------------- MATRIX --------------------------+ |
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
public class Matrix {
    private static final long SLOT_SIZE = 8;

    private final int capacity;
    private final int numHeaders;
    private final int numRows;
    private final int numColumns;
    private final int numSlotsPerCell;

    private final long headerByteSize;
    private final long cellByteSize;
    private final long rowByteSize;
    private final long matrixPartSize;
    private final long fullBlockSize;

    private final Arena arena;
    private final MemorySegment segment;


    public Matrix(int capacity, int numHeaders, int numRows, int numColumns, int numSlotsPerCell) {
        this.capacity = capacity;
        this.numHeaders = numHeaders;
        this.numRows = numRows;
        this.numColumns = numColumns;
        this.numSlotsPerCell = numSlotsPerCell;

        this.headerByteSize  = numHeaders * SLOT_SIZE;
        this.cellByteSize    = numSlotsPerCell * SLOT_SIZE;
        this.rowByteSize     = numColumns * cellByteSize;
        this.matrixPartSize  = numRows * rowByteSize;
        this.fullBlockSize   = headerByteSize + matrixPartSize;

        this.arena   = Arena.ofShared();
        this.segment = arena.allocate(capacity * fullBlockSize);
    }

    // ------------------------------------------------------------
    // HEADER: set / get
    // ------------------------------------------------------------

    public void setHeaderInt(long m, long h, int val) {
        segment.set(ValueLayout.JAVA_INT, headerOffset(m, h), val);
    }

    public int getHeaderInt(long m, long h) {
        return segment.get(ValueLayout.JAVA_INT, headerOffset(m, h));
    }

    public void setHeaderFloat(long m, long h, float val) {
        segment.set(ValueLayout.JAVA_FLOAT, headerOffset(m, h), val);
    }

    public float getHeaderFloat(long m, long h) {
        return segment.get(ValueLayout.JAVA_FLOAT, headerOffset(m, h));
    }

    public void setHeaderLong(long m, long h, long val) {
        segment.set(ValueLayout.JAVA_LONG, headerOffset(m, h), val);
    }

    public long getHeaderLong(long m, long h) {
        return segment.get(ValueLayout.JAVA_LONG, headerOffset(m, h));
    }

    public void setHeaderDouble(long m, long h, double val) {
        segment.set(ValueLayout.JAVA_DOUBLE, headerOffset(m, h), val);
    }

    public double getHeaderDouble(long m, long h) {
        return segment.get(ValueLayout.JAVA_DOUBLE, headerOffset(m, h));
    }

    // ------------------------------------------------------------
    // CELLS: set / get (n = Slot-Index)
    // ------------------------------------------------------------

    public void setCellInt(long index, long row, long col, long n, int value) {
        segment.set(ValueLayout.JAVA_INT, cellOffset(index, row, col, n), value);
    }

    public int getCellInt(long index, long row, long col, long n) {
        return segment.get(ValueLayout.JAVA_INT, cellOffset(index, row, col, n));
    }

    public void setCellFloat(long index, long row, long col, long n, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, cellOffset(index, row, col, n), value);
    }

    public float getCellFloat(long index, long row, long col, long n) {
        return segment.get(ValueLayout.JAVA_FLOAT, cellOffset(index, row, col, n));
    }

    public void setCellLong(long index, long row, long col, long n, long value) {
        segment.set(ValueLayout.JAVA_LONG, cellOffset(index, row, col, n), value);
    }

    public long getCellLong(long index, long row, long col, long n) {
        return segment.get(ValueLayout.JAVA_LONG, cellOffset(index, row, col, n));
    }

    public void setCellDouble(long index, long row, long col, long n, double value) {
        segment.set(ValueLayout.JAVA_DOUBLE, cellOffset(index, row, col, n), value);
    }

    public double getCellDouble(long index, long row, long col, long n) {
        return segment.get(ValueLayout.JAVA_DOUBLE, cellOffset(index, row, col, n));
    }


    // --- convenient ---

    private long headerOffset(long m, long h) {
        return (m * fullBlockSize) + (h << 3);
    }

    private long cellOffset(long m, long r, long c, long n) {
        return (m * fullBlockSize) + headerByteSize + (r * rowByteSize) + (c * cellByteSize) + (n << 3);
    }

    // --- Getter / Setter ---

    public int getCapacity() {
        return capacity;
    }

    public int getNumHeaders() {
        return numHeaders;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getNumColumns() {
        return numColumns;
    }

    public int getNumSlotsPerCell() {
        return numSlotsPerCell;
    }

    public void close() {
        if (arena != null) {
            arena.close();
        }
    }
}
