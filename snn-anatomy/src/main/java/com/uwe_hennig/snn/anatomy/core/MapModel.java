/**
 * @(#)MapModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * MapModel
 *
 * @author Uwe Hennig
 */
public class MapModel {
    private final Arena         arena;
    private final MemorySegment segment;

    private final int size;

    public MapModel(int size) {
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(8L * size, 8);
        this.size = size;
    }

    private static final VarHandle INT_HANDLE    = ValueLayout.JAVA_INT.varHandle();
    private static final VarHandle LONG_HANDLE   = ValueLayout.JAVA_LONG.varHandle();
    private static final VarHandle FLOAT_HANDLE  = ValueLayout.JAVA_FLOAT.varHandle();
    private static final VarHandle DOUBLE_HANDLE = ValueLayout.JAVA_DOUBLE.varHandle();

    public void close() {
        arena.close();
    }

    // --- PUT-Methoden ---

    public void put(int index, int value) {
        INT_HANDLE.set(segment, 0L, index, value);
    }

    public void putVolatile(int index, int value) {
        INT_HANDLE.setVolatile(segment, 0L, index, value);
    }

    public void put(int index, long value) {
        LONG_HANDLE.set(segment, 0L, index, value);
    }

    public void putVolatile(int index, long value) {
        LONG_HANDLE.setVolatile(segment, 0L, index, value);
    }

    public void put(int index, float value) {
        FLOAT_HANDLE.set(segment, 0L, index, value);
    }

    public void putVolatile(int index, float value) {
        FLOAT_HANDLE.setVolatile(segment, 0L, index, value);
    }

    public void put(int index, double value) {
        DOUBLE_HANDLE.set(segment, 0L, index, value);
    }

    public void putVolatile(int index, double value) {
        DOUBLE_HANDLE.setVolatile(segment, 0L, index, value);
    }

    // --- GET-Methoden ---

    public int getInt(int index) {
        return (int)INT_HANDLE.get(segment, 0L, index);
    }

    public long getLong(int index) {
        return (long)LONG_HANDLE.get(segment, 0L, index);
    }

    public float getFloat(int index) {
        return (float)FLOAT_HANDLE.get(segment, 0L, index);
    }

    public double getDouble(int index) {
        return (double) DOUBLE_HANDLE.get(segment, 0L, index);
    }

    public int getIntVolatile(int index) {
        return (int)INT_HANDLE.getVolatile(segment, 0L, index);
    }

    public long getLongVolatile(int index) {
        return (long)LONG_HANDLE.getVolatile(segment, 0L, index);
    }

    public float getFloatVolatile(int index) {
        return (float)FLOAT_HANDLE.getVolatile(segment, 0L, index);
    }

    public double getDoubleVolatile(int index) {
        return (double) DOUBLE_HANDLE.getVolatile(segment, 0L, index);
    }
}
