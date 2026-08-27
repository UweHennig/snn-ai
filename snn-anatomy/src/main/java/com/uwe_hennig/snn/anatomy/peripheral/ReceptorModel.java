/**
 * @(#)ReceptorModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * ReceptorModel
 *
 * @author Uwe Hennig
 */
public class ReceptorModel {
    final int capacity;

    final int rows;
    final int cols;

    Arena         arena;
    MemorySegment segment;

    // @formatter:off
    static final VarHandle VH_INT      = ValueLayout.JAVA_INT.withByteAlignment(4).varHandle();
    static final VarHandle VH_FLOAT    = ValueLayout.JAVA_FLOAT.withByteAlignment(4).varHandle();

    static final long      HEADER_SIZE = 8; // sizeof([HEADER_DATA])
    static final long      TARGET_SIZE = 8; // sizeof([TARGET_ID, TARGET_TYPE])

    final long rowSize;        // columns * TARGET_SIZE
    final long receptorSize;   // HEADER_SIZE + rows * rowSize

    // ----- public -----

    public ReceptorModel(int numReceptors, int rows, int columns) {
        assert numReceptors >= 1 : "invalid receptor number";
        assert rows >= 1         : "invalid rows in ReceptorModel";
        assert columns >= 1      : "invalid columns in ReceptorModel";

        this.capacity = numReceptors;
        this.rows = rows;
        this.cols = columns;

        this.rowSize = columns * TARGET_SIZE;
        this.receptorSize = HEADER_SIZE + (rows * rowSize);

        this.arena = Arena.ofShared();
        this.segment = arena.allocate(numReceptors * receptorSize);
    }
    // @formatter:on

    public void close() {
        arena.close();
        arena = null;
    }

    public int getCapacity() {
        return capacity;
    }

    // ----- getter/setter -----
    void setTargetId(int index, int row, int col, int value) {
        long receptorIdx = index * receptorSize;
        long matrixStart = receptorIdx + HEADER_SIZE;
        long offsetTargetId = matrixStart + (row * rowSize) + (col * TARGET_SIZE);

        VH_INT.set(segment, offsetTargetId, value);

    }

    int getTargetId(int index, int row, int col) {
        long receptorIdx = index * receptorSize;
        long matrixStart = receptorIdx + HEADER_SIZE;
        long offsetTargetId = matrixStart + (row * rowSize) + (col * TARGET_SIZE);
        return (int) VH_INT.get(segment, offsetTargetId);
    }

    void setTargetType(int index, int row, int col, int value) {
        long receptorIdx = index * receptorSize;
        long matrixStart = receptorIdx + HEADER_SIZE;

        long offset = matrixStart + (row * rowSize) + (col * TARGET_SIZE) + 4;

        VH_INT.set(segment, offset, value);
    }

    int getTargetType(int index, int row, int col) {
        long offsetTargetType = (index * receptorSize) + HEADER_SIZE + (row * rowSize) + (col * TARGET_SIZE) + 4;
        return (int) VH_INT.get(segment, offsetTargetType);
    }

    public void setIntakeDistance(int index, float value) {
        long offset = (index * receptorSize) + 4;
        VH_FLOAT.set(segment, offset, value);
    }

    public float getIntakeDistance(int index) {
        long offset = (index * receptorSize) + 4;
        return (float) VH_FLOAT.get(segment, offset);
    }

}
