/**
 * @(#)TransferSegment.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/**
 * TransferSegment
 * @formatter:off
 * <pre>
┌────────────────────────────────┐
│ MetaInfo                       │
│  00: numBlocks     : int       │
│  04: startOffset   : int       │
│  08: headerSize    : int       │
│  12: entrySize     : int       │
│  16: endOffset     : int       │
├────────────────────────────────┤
│ BlockHeader                    │
│  20: state         : int       │
│  24: count         : int       │
│  28: capacity      : int       │
│  32: stimulusType  : int       │
│  36: nextBlock     : int       │
├────────────────────────────────┤
│ Entry 0                        │
│  40: targetId      : int       │
│  44: targetType    : int       │
│  48: value         : float     │
├────────────────────────────────┤
│ Entry 1                        │
│  52: targetId      : int       │
│  56: targetType    : int       │
│  60: value         : float     │
├────────────────────────────────┤
│ ...                            │
├────────────────────────────────┤
│ next Block                     │
└────────────────────────────────┘
 * </pre>
 * @formatter:on
 * @author Uwe Hennig
 */
public final class TransferSegment {
    private static final int META_SIZE   = 20;
    private static final int HEADER_SIZE = 20;
    private static final int ENTRY_SIZE  = 12;

    private Arena         arena;
    private MemorySegment segment;

    public TransferSegment(int size) {
        assert size > META_SIZE + HEADER_SIZE + ENTRY_SIZE : "Size too small!";

        this.arena = Arena.ofShared();
        this.segment = arena.allocate(size);
        initMeta();
    }

    void setEntry(int blockOffset, int position, int targetId, int targetType, float value) {
        setTargetId(blockOffset, position, targetId);
        setTargetType(blockOffset, position, targetType);
        setValue(blockOffset, position, value);
    }

    void setTargetId(int blockOffset, int position, int value) {
        int startEntryOffset = blockOffset + HEADER_SIZE + position * ENTRY_SIZE;
        segment.set(ValueLayout.JAVA_INT, startEntryOffset + 0, value);
    }

    int getTargetId(int blockOffset, int position) {
        int startEntryOffset = blockOffset + HEADER_SIZE + position * ENTRY_SIZE;
        return segment.get(ValueLayout.JAVA_INT, startEntryOffset + 0);
    }

    void setTargetType(int blockOffset, int position, int value) {
        int startEntryOffset = blockOffset + HEADER_SIZE + position * ENTRY_SIZE;
        segment.set(ValueLayout.JAVA_INT, startEntryOffset + 4, value);
    }

    int getTargetType(int blockOffset, int position) {
        int startEntryOffset = blockOffset + HEADER_SIZE + position * ENTRY_SIZE;
        return segment.get(ValueLayout.JAVA_INT, startEntryOffset + 4);
    }

    void setValue(int blockOffset, int position, float value) {
        int startEntryOffset = blockOffset + HEADER_SIZE + position * ENTRY_SIZE;
        segment.set(ValueLayout.JAVA_FLOAT, startEntryOffset + 8, value);
    }

    float getValue(int blockOffset, int position) {
        int startEntryOffset = blockOffset + HEADER_SIZE + position * ENTRY_SIZE;
        return segment.get(ValueLayout.JAVA_FLOAT, startEntryOffset + 8);
    }

    int allocateBlock(int capacity) {
        int offset = getEndOffset();

        setState(offset, -1);
        setCount(offset, 0);
        setCapacity(offset, capacity);
        setStimulusType(offset, -1);
        setNextBlock(offset, -1);

        int newEnd = offset + 12 * capacity;
        setEndOffset(newEnd);

        return offset;
    }

    void setState(long blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 0, value);
    }

    int getState(long blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 0);
    }

    void setCount(long blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 4, value);
    }

    int getCount(long blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 4);
    }

    void setCapacity(long blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 8, value);
    }

    int getCapacity(long blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 8);
    }

    void setStimulusType(long blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 12, value);
    }

    int getStimulusType(long blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 12);
    }

    void setNextBlock(long blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 16, value);
    }

    int getNextBlock(long blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 16);
    }

    private void initMeta() {
        setNumBlocks(0);
        setStartOffset(20);
        setHeaderSize(HEADER_SIZE);
        setEntrySize(ENTRY_SIZE);
        setEndOffset(40);
    }

    void setNumBlocks(int value) {
        segment.set(ValueLayout.JAVA_INT, 0L, value);
    }

    int getNumBlocks() {
        return segment.get(ValueLayout.JAVA_INT, 0L);
    }

    void setStartOffset(int value) {
        segment.set(ValueLayout.JAVA_INT, 4L, value);
    }

    int getStartOffset() {
        return segment.get(ValueLayout.JAVA_INT, 4L);
    }

    void setHeaderSize(int value) {
        segment.set(ValueLayout.JAVA_INT, 8L, value);
    }

    int getHeaderSize() {
        return segment.get(ValueLayout.JAVA_INT, 8L);
    }

    void setEntrySize(int value) {
        segment.set(ValueLayout.JAVA_INT, 12L, value);
    }

    int getEntrySize() {
        return segment.get(ValueLayout.JAVA_INT, 12L);
    }

    void setEndOffset(int value) {
        segment.set(ValueLayout.JAVA_INT, 16L, value);
    }

    int getEndOffset() {
        return segment.get(ValueLayout.JAVA_INT, 16);
    }

    public void close() {
        if (arena != null) {
            arena.close();
        }
    }
}
