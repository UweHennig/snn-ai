/**
 * @(#)SnnBitSet.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static java.lang.foreign.ValueLayout.JAVA_LONG;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;

/**
 * SnnBitSet
 *
 * @author Uwe Hennig
 */
public class SnnBitSet implements AutoCloseable {
    private final Arena   arena;
    private MemorySegment segment;
    private long          byteSize; // Immer ein Vielfaches von 8

    public SnnBitSet(long initialBits) {
        this.arena = Arena.ofConfined();
        this.byteSize = ((initialBits + 63) / 64) * 8;
        this.segment = arena.allocate(byteSize);
    }

    public void set(long bitIndex) {
        long longIndex = bitIndex >> 6; // bitIndex / 64
        int bitPosition = (int) (bitIndex & 63); // bitIndex % 64
        long offset = longIndex << 3; // longIndex * 8 (Byte-Offset)

        if (offset >= byteSize) {
            ensureCapacity(offset + 8);
        }

        long currentLong = segment.get(JAVA_LONG, offset);
        long newLong = currentLong | (1L << bitPosition);
        segment.set(JAVA_LONG, offset, newLong);
    }

    public void unset(long bitIndex) {
        long longIndex = bitIndex >> 6;
        int bitPosition = (int) (bitIndex & 63);
        long offset = longIndex << 3;

        if (offset >= byteSize) {
            return;
        }

        long currentLong = segment.get(JAVA_LONG, offset);
        long newLong = currentLong & ~(1L << bitPosition);
        segment.set(JAVA_LONG, offset, newLong);
    }

    public boolean get(long bitIndex) {
        long longIndex = bitIndex >> 6;
        int bitPosition = (int) (bitIndex & 63);
        long offset = longIndex << 3;

        if (offset >= byteSize) {
            return false;
        }

        long currentLong = segment.get(JAVA_LONG, offset);
        return (currentLong & (1L << bitPosition)) != 0;
    }

    public int cardinality() {
        int count = 0;
        for (long offset = 0; offset < byteSize; offset += 8) {
            count += Long.bitCount(segment.get(JAVA_LONG, offset));
        }
        return count;
    }

    /**
     * Finds the highest-set bit position.
     */
    public long highestBit() {
        for (long offset = byteSize - 8; offset >= 0; offset -= 8) {
            long val = segment.get(JAVA_LONG, offset);
            if (val != 0) {
                // Long.numberOfLeadingZeros nutzt die CPU-Instruktion LZCNT
                int bitInLong = 63 - Long.numberOfLeadingZeros(val);
                return (offset << 3) + bitInLong; // (offset/8 * 64) + bitInLong
            }
        }
        return -1;
    }

    /**
     * Finds the next set bit starting at position 'from'.
     */
    public long nextBit(long from) {
        long longIndex = from >> 6;
        long offset = longIndex << 3;

        if (offset >= byteSize) {
            return -1;
        }

        long val = segment.get(JAVA_LONG, offset);
        val &= -(1L << (from & 63)); // Masks all bits below 'from'

        if (val != 0) {
            return (longIndex << 6) + Long.numberOfTrailingZeros(val);
        }

        for (offset += 8; offset < byteSize; offset += 8) {
            val = segment.get(JAVA_LONG, offset);
            if (val != 0) {
                return (offset << 3) + Long.numberOfTrailingZeros(val);
            }
        }
        return -1;
    }

    private void ensureCapacity(long minByteSize) {
        long targetSize = Math.max(byteSize * 2, ((minByteSize + 7) / 8) * 8);
        MemorySegment newSegment = arena.allocate(targetSize);
        MemorySegment.copy(segment, 0, newSegment, 0, byteSize);
        this.segment = newSegment;
        this.byteSize = targetSize;
    }

    @Override
    public void close() {
        if (arena.scope().isAlive()) {
            arena.close();
        }
    }
}
