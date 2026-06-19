/**
 * @(#)SnnBitSet.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * SnnBitSet
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class SnnBitSet implements AutoCloseable {
    private final Arena arena;
    private MemorySegment segment;
    private long byteSize;

    public SnnBitSet(long initialBits) {
        this.arena = Arena.ofConfined();
        this.byteSize = (initialBits + 7) / 8;
        this.segment = arena.allocate(byteSize);
    }

    public void set(long bitIndex) {
        long byteIndex = bitIndex >> 3; // bitIndex / 8
        int bitPosition = (int) (bitIndex & 7); // bitIndex % 8

        if (byteIndex >= byteSize) {
            ensureCapacity(byteIndex + 1);
        }

        byte currentByte = segment.get(java.lang.foreign.ValueLayout.JAVA_BYTE, byteIndex);
        byte newByte = (byte) (currentByte | (1 << bitPosition));
        segment.set(java.lang.foreign.ValueLayout.JAVA_BYTE, byteIndex, newByte);
    }

    public void unset(long bitIndex) {
        long byteIndex = bitIndex >> 3;
        int bitPosition = (int) (bitIndex & 7);

        if (byteIndex >= byteSize) {
            return;
        }

        byte currentByte = segment.get(java.lang.foreign.ValueLayout.JAVA_BYTE, byteIndex);
        byte newByte = (byte) (currentByte & ~(1 << bitPosition));
        segment.set(java.lang.foreign.ValueLayout.JAVA_BYTE, byteIndex, newByte);
    }

    public boolean get(long bitIndex) {
        long byteIndex = bitIndex >> 3;
        int bitPosition = (int) (bitIndex & 7);

        if (byteIndex >= byteSize) {
            return false;
        }

        byte currentByte = segment.get(java.lang.foreign.ValueLayout.JAVA_BYTE, byteIndex);
        return (currentByte & (1 << bitPosition)) != 0;
    }

    private void ensureCapacity(long newByteSize) {
        long targetSize = Math.max(byteSize * 2, newByteSize);
        MemorySegment newSegment = arena.allocate(targetSize);

        MemorySegment.copy(segment, 0, newSegment, 0, byteSize);
        this.segment = newSegment;
        this.byteSize = targetSize;
    }

    @Override
    public void close() {
        arena.close();
    }
}
