/**
 * @(#)SynapseChainModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * SynapseChainModel
 * @formatter:off
 *
 * Structure:
 * [OFFSET_DATA_END]
 * [BLOCK_CAPACITY][NUM_ELMENTS][NEXT_BLOCK][IDENTIFIER_1][IDENTIFIER_2]...
 * [BLOCK_CAPACITY][NUM_ELMENTS][NEXT_BLOCK][IDENTIFIER_1][IDENTIFIER_2]...
 *
 * [BLOCK_CAPACITY: int]        // 4 bytes
 * [TOTAL_ELEMENTS: int]        // 4 bytes
 * [NUM_ELEMENTS: int]          // 4 bytes
 * [NEXT_BLOCK: int]            // 4 bytes
 * [IDENTIFIER_1: int]          // 4 bytes
 * [IDENTIFIER_2: int]          // 4 bytes
 *
 * @formatter:on
 * @author Uwe Hennig
 */
@Deprecated
public final class SynapseChainModel {
    private static final int LIST_ARRAY_START = 4;

    private Arena         arena;
    private MemorySegment segment;

    @Deprecated
    public SynapseChainModel(int size) {
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(size);
        setLastOffset(4);
    }

    // ---- Public ----

    @Deprecated
    public int allocate(int blockCapacity) {
        int lastendOffset = getLastOffset();
        int newEndOffset = lastendOffset + blockCapacity * 4 + 16;
        setLastOffset(newEndOffset);

        setBlockCapacity(lastendOffset, blockCapacity);
        setTotalElements(lastendOffset, 0);
        setNumElements(lastendOffset, 0);
        setNextBlock(lastendOffset, -1);

        return lastendOffset;
    }

    @Deprecated
    public void addSynapseId(int base, int synapseId) {
        int startOffset = base;
        int currentOffset = base;

        while (true) {
            int cap = getBlockCapacity(currentOffset);
            int num = getNumElements(currentOffset);

            if (num < cap) {
                setTotalElements(startOffset, getTotalElements(startOffset) + 1);
                setNumElements(currentOffset, num + 1);
                setSynapseId(currentOffset, num, synapseId);
                return;
            }

            int nextBlockOffset = getNextBlock(currentOffset);
            if (nextBlockOffset == -1) {
                int newBlockOffset = allocate(cap);
                setNextBlock(currentOffset, newBlockOffset);
                currentOffset = newBlockOffset;
            } else {
                currentOffset = nextBlockOffset;
            }
        }
    }

    @Deprecated
    public int [] getFirstList() {
        return getList(LIST_ARRAY_START);
    }

    @Deprecated
    public int[] getSynapses(int offset) {
        return getList(offset);
    }

    @Deprecated
    public void close() {
        if (arena != null) {
            arena.close();
        }
    }

    // --- Getter/Setter ---

    @Deprecated
    void setLastOffset(int offset) {
        segment.set(ValueLayout.JAVA_INT, 0L, offset);
    }

    @Deprecated
    int getLastOffset() {
        return segment.get(ValueLayout.JAVA_INT, 0L);
    }

    @Deprecated
    void setSynapseId(int base, int position, int id) {
        segment.set(ValueLayout.JAVA_INT, base + 16 + position * 4, id);
    }

    @Deprecated
    void setBlockCapacity(int base, int blockCapacity) {
        segment.set(ValueLayout.JAVA_INT, base, blockCapacity);
    }

    @Deprecated
    int getBlockCapacity(int base) {
        return segment.get(ValueLayout.JAVA_INT, base);
    }

    @Deprecated
    void setTotalElements(int base, int total) {
        segment.set(ValueLayout.JAVA_INT, base + 4, total);
    }

    @Deprecated
    int getTotalElements(int base) {
        return segment.get(ValueLayout.JAVA_INT, base + 4);
    }

    @Deprecated
    void setNumElements(int base, int numElements) {
        segment.set(ValueLayout.JAVA_INT, base + 8, numElements);
    }

    @Deprecated
    int getNumElements(int base) {
        return segment.get(ValueLayout.JAVA_INT, base + 8);
    }

    @Deprecated
    void setNextBlock(int base, int nextBlock) {
        segment.set(ValueLayout.JAVA_INT, base + 12, nextBlock);
    }

    @Deprecated
    int getNextBlock(int base) {
        return segment.get(ValueLayout.JAVA_INT, base + 12);
    }

    // --- convenient ---

    private int[] getList(int offset) {
        int total = getTotalElements(offset);
        int[] result = new int[total];

        int pos = 0;
        int current = offset;

        int next = 0;
        int num = 0;

        while (true) {
            num = getNumElements(current);

            MemorySegment.copy(segment, ValueLayout.JAVA_INT, current + 16, result, pos, num);
            pos += num;

            next = getNextBlock(current);
            if (next == -1) {
                break;
            }

            current = next;
        }

        return result;
    }
}
