/// @(#)BufferedTransferSegment.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/// BufferedTransferSegment
/// ````
/// ┌─────────────────────────────────┐
/// │ MetaInfo  (32 bytes)            │
/// │  00: magic            : int     │
/// │  04: metaInfoSize     : int     │
/// │  08: blockHeaderSize  : int     │
/// │  12: entrySize        : int     │
/// │  16: entryHeaderSize  : int     │
/// │  20: numQueueElements : int     │
/// │  24: numBlocks        : int     │
/// │  28: endOffset        : int     │
/// ├─────────────────────────────────┤
/// │ Block Header (12 bytes)         │
/// │  32: entries        : int       │
/// │  36: srcId          : int       │
/// │  40: srcType        : int       │
/// ├─────────────────────────────────┤
/// │ Entry 0 (56 bytes)              │
/// │  44: targetId        : int      │
/// │  48: targetType      : int      │
/// | Stimulus                        │
/// │  52: stimulus_status : int      │
/// │  56: stimulus        : float[3] │
/// | Stimulus Feedback Time          │
/// │  68: fb_time_status  : int      │
/// │  72: fb_time         : float[3] │
/// | Stimulus Feedback Value         │
/// │  84: fb_value_status : int      │
/// │  88: fb_value        : float[3] │
/// ├─────────────────────────────────┤
/// │ Entry 1 (56 bytes)              │
/// │  100: targetId        : int     │
/// │  104: targetType      : int     │
/// | Stimulus                        │
/// │  108: stimulus_status : int     │
/// │  112: stimulus        : float[3]│
/// | Stimulus Feedback Time          │
/// │  124: fb_time_status  : int     │
/// │  128: fb_time         : float[3]│
/// | Stimulus Feedback Value         │
/// │  140: fb_value_status : int     │
/// │  144: fb_value        : float[3]│
/// ├─────────────────────────────────┤
/// │  156: next Block                │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
///
/// All neuron elements have their own mini-queue for each stimulus
/// A feedback loop can have its own list of start neuron
/// elements. These transmit the stimuli via their graph structure.
/// ````
///
/// @author Uwe Hennig
public final class BufferedTransferSegment {
    static final int   MAGIC       = 0x42545331; // BTS
    static final int   VALUE_SIZE  = 4;
    static final float EMTPY_VALUE = Float.NaN;

    static final int META_SIZE          = 8 * VALUE_SIZE;
    static final int BLOCK_HEADER_SIZE  = 3 * VALUE_SIZE;
    static final int ENTRY_HEADER_SIZE  = 2 * VALUE_SIZE;
    static final int NUM_QUEUE_ELEMENTS = 3;
    static final int NUM_STIMULI_TYPES  = 3;
    static final int QUEUE_SIZE         = (NUM_QUEUE_ELEMENTS + 1) * VALUE_SIZE;

    static final int ENTRY_SIZE = ENTRY_HEADER_SIZE + QUEUE_SIZE * NUM_STIMULI_TYPES;
    static final int START_OFFSET = META_SIZE;

    final Arena         arena;
    final MemorySegment segment;

    public BufferedTransferSegment(int size) {
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(size);
        initMeta();
    }

    // --- META ---

    void setMagic(int value) {
        segment.set(ValueLayout.JAVA_INT, 0L, value);
    }

    int getMagic() {
        return segment.get(ValueLayout.JAVA_INT, 0L);
    }

    void setMetaInfoSize(int value) {
        segment.set(ValueLayout.JAVA_INT, 4L, value);
    }

    int getMetaInfoSize() {
        return segment.get(ValueLayout.JAVA_INT, 4L);
    }

    void setBlockHeaderSize(int value) {
        segment.set(ValueLayout.JAVA_INT, 8L, value);
    }

    int getBlockHeaderSize() {
        return segment.get(ValueLayout.JAVA_INT, 8L);
    }

    void setEntrySize(int value) {
        segment.set(ValueLayout.JAVA_INT, 12L, value);
    }

    int getEntrySize() {
        return segment.get(ValueLayout.JAVA_INT, 12L);
    }

    void setEntryHeaderSize(int value) {
        segment.set(ValueLayout.JAVA_INT, 16L, value);
    }

    int getEntryHeaderSize() {
        return segment.get(ValueLayout.JAVA_INT, 16L);
    }

    void setNumQueueElments(int value) {
        segment.set(ValueLayout.JAVA_INT, 20L, value);
    }

    int getNumQueueElments() {
        return segment.get(ValueLayout.JAVA_INT, 20L);
    }

    void setNumBlocks(int value) {
        segment.set(ValueLayout.JAVA_INT, 24L, value);
    }

    int getNumBlocks() {
        return segment.get(ValueLayout.JAVA_INT, 24L);
    }

    void setEndOffset(int value) {
        segment.set(ValueLayout.JAVA_INT, 28L, value);
    }

    int getEndOffset() {
        return segment.get(ValueLayout.JAVA_INT, 28L);
    }

    private void initMeta() {
        setMagic(MAGIC);
        setMetaInfoSize(META_SIZE);
        setBlockHeaderSize(BLOCK_HEADER_SIZE);
        setEntrySize(ENTRY_SIZE);
        setEntryHeaderSize(ENTRY_HEADER_SIZE);
        setNumQueueElments(NUM_QUEUE_ELEMENTS);
        setNumBlocks(0);
        setEndOffset(META_SIZE);
    }

    // --- Block ---

    // returns the offset, which ist an index of receptor view
    public int allocateBlock(int entries, int srcId, int srcType) {
        int endBlock = getEndOffset();
        setEndOffset(endBlock + entries * ENTRY_SIZE + BLOCK_HEADER_SIZE);

        int numBlocks = getNumBlocks();
        setNumBlocks(numBlocks + 1);

        setSrcId(endBlock, srcId);
        setSrcType(endBlock, srcType);
        setEntries(endBlock, entries);

        return endBlock;
    }

    void setEntries(int blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset, value);
    }

    public int getEntries(int blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset);
    }

    public void setSrcId(int blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 4L, value);
    }

    public int getSrcId(int blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 4L);
    }

    public void setSrcType(int blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 8L, value);
    }

    public int getSrcType(int blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 8L);
    }

    // --- Enty Header ---

    public void setTrgId(int blockOffset, int entry, int value) {
        int entryOffset = blockOffset + BLOCK_HEADER_SIZE + entry * ENTRY_SIZE;
        segment.set(ValueLayout.JAVA_INT, entryOffset, value);
    }

    public int getTrgId(int blockOffset, int entry) {
        int entryOffset = blockOffset + BLOCK_HEADER_SIZE + entry * ENTRY_SIZE;
        return segment.get(ValueLayout.JAVA_INT, entryOffset);
    }

    public void setTrgType(int blockOffset, int entry, int value) {
        int entryOffset = blockOffset + BLOCK_HEADER_SIZE + entry * ENTRY_SIZE + 4;
        segment.set(ValueLayout.JAVA_INT, entryOffset, value);
    }

    public int getTrgType(int blockOffset, int entry) {
        int entryOffset = blockOffset + BLOCK_HEADER_SIZE + entry * ENTRY_SIZE + 4;
        return segment.get(ValueLayout.JAVA_INT, entryOffset);
    }

    // --- Queues ---

    public boolean offerStimulus(int blockOffset, int entry, float value) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_SIZE * entry + ENTRY_HEADER_SIZE;
        return BufferedTransferHelper.offer(segment, queueOffset, value);
    }

    public float pollStimulus(int blockOffset, int entry) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_SIZE * entry + ENTRY_HEADER_SIZE;
        return BufferedTransferHelper.poll(segment, queueOffset);
    }

    public boolean offerFbTime(int blockOffset, int entry, float value) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_SIZE * entry + ENTRY_HEADER_SIZE + QUEUE_SIZE;
        return BufferedTransferHelper.offer(segment, queueOffset, value);
    }

    public float pollFbTime(int blockOffset, int entry) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_SIZE * entry + ENTRY_HEADER_SIZE + QUEUE_SIZE;
        return BufferedTransferHelper.poll(segment, queueOffset);
    }

    public boolean offerFbValue(int blockOffset, int entry, float value) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_SIZE * entry + ENTRY_HEADER_SIZE + QUEUE_SIZE * 2;
        return BufferedTransferHelper.offer(segment, queueOffset, value);
    }

    public float pollFbValue(int blockOffset, int entry) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_SIZE * entry + ENTRY_HEADER_SIZE + QUEUE_SIZE * 2;
        return BufferedTransferHelper.poll(segment, queueOffset);
    }

    public void close() {
        if (arena != null) {
            arena.close();
        }
    }
}
