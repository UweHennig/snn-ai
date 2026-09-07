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
public class SynapseChainModel {
    private Arena         arena;
    private MemorySegment segment;

    public SynapseChainModel(int size) {
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(size);
        setLastOffset(4);
    }

    // ---- Public ----

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

    public void addSynapseId(int base, int synapseId) {
        int startOffset = base;
        int currentOffset = base;

        while (true) {
            int cap = getBlockCapacity(currentOffset);
            int num = getNumElements(currentOffset);

            if (num < cap) {
                setSynapseId(currentOffset, num, synapseId);
                setNumElements(currentOffset, num + 1);
                setTotalElements(startOffset, getTotalElements(startOffset) + 1);
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

    public int[] getSynapses(int offset) {
        int total = getTotalElements(offset);
        int[] result = new int[total];

        int pos = 0;
        int current = offset;

        while (true) {
            int num = getNumElements(current);

            MemorySegment.copy(segment, ValueLayout.JAVA_INT, current + 16, result, pos, num);
            pos += num;

            int next = getNextBlock(current);
            if (next == -1) {
                break;
            }

            current = next;
        }

        return result;
    }


    public void close() throws Exception {
        if (arena != null) {
            arena.close();
        }
    }

    // --- Getter/Setter ---

    void setLastOffset(int offset) {
        segment.set(ValueLayout.JAVA_INT, 0L, offset);
    }

    int getLastOffset() {
        return segment.get(ValueLayout.JAVA_INT, 0L);
    }

    void setSynapseId(int base, int position, int id) {
        segment.set(ValueLayout.JAVA_INT, base + 16 + position * 4, id);
    }

    void setBlockCapacity(int base, int blockCapacity) {
        segment.set(ValueLayout.JAVA_INT, base, blockCapacity);
    }

    int getBlockCapacity(int base) {
        return segment.get(ValueLayout.JAVA_INT, base);
    }

    void setTotalElements(int base, int total) {
        segment.set(ValueLayout.JAVA_INT, base + 4, total);
    }

    int getTotalElements(int base) {
        return segment.get(ValueLayout.JAVA_INT, base + 4);
    }

    void setNumElements(int base, int numElements) {
        segment.set(ValueLayout.JAVA_INT, base + 8, numElements);
    }

    int getNumElements(int base) {
        return segment.get(ValueLayout.JAVA_INT, base + 8);
    }

    void setNextBlock(int base, int nextBlock) {
        segment.set(ValueLayout.JAVA_INT, base + 12, nextBlock);
    }

    int getNextBlock(int base) {
        return segment.get(ValueLayout.JAVA_INT, base + 12);
    }
}
