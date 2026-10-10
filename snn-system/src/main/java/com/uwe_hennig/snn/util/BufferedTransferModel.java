/// @(#)BufferedTransferModel.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/// BufferedTransferModel
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
/// │  36: srcNeuronId    : int       │
/// │  40: srcId          : int       │
/// │  44: srcType        : int       │
/// ├─────────────────────────────────┤
/// │ Entry 0 (56 bytes)              │
/// │  48: targetId        : int      │
/// │  52: targetType      : int      │
/// | Stimulus                        │
/// │  56: stimulus_status : int      │
/// │  60: stimulus        : float[3] │
/// | Stimulus Feedback Time          │
/// │  72: fb_time_status  : int      │
/// │  76: fb_time         : float[3] │
/// | Stimulus Feedback Value         │
/// │  88: fb_value_status : int      │
/// │  92: fb_value        : float[3] │
/// ├─────────────────────────────────┤
/// │ Entry 1 (56 bytes)              │
/// │  104: targetId        : int     │
/// │  108: targetType      : int     │
/// | Stimulus                        │
/// │  112: stimulus_status : int     │
/// │  116: stimulus        : float[3]│
/// | Stimulus Feedback Time          │
/// │  128: fb_time_status  : int     │
/// │  132: fb_time         : float[3]│
/// | Stimulus Feedback Value         │
/// │  136: fb_value_status : int     │
/// │  140: fb_value        : float[3]│
/// ├─────────────────────────────────┤
/// │  152: next Block                │
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
public final class BufferedTransferModel {
    static final int   MAGIC       = 0x42545331; // BTS
    static final int   VALUE_SIZE  = 4;
    static final float EMTPY_VALUE = Float.NaN;

    static final int META_SIZE          = 8 * VALUE_SIZE;
    static final int BLOCK_HEADER_SIZE  = 4 * VALUE_SIZE;
    static final int ENTRY_HEADER_SIZE  = 2 * VALUE_SIZE;
    static final int NUM_QUEUE_ELEMENTS = 3;
    static final int NUM_STIMULI_TYPES  = 3;
    static final int QUEUE_SIZE         = (NUM_QUEUE_ELEMENTS + 1) * VALUE_SIZE;

    static final int ENTRY_SIZE = ENTRY_HEADER_SIZE + QUEUE_SIZE * NUM_STIMULI_TYPES;
    static final int START_OFFSET = META_SIZE;

    final Arena         arena;
    final MemorySegment segment;

    public BufferedTransferModel(int size) {
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

    protected void setMetaInfoSize(int value) {
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
    public int claimSlot(int entries, int srcNeuronId, int srcId, int srcType) {
        int endBlock = getEndOffset();
        setEndOffset(endBlock + entries * ENTRY_SIZE + BLOCK_HEADER_SIZE);

        int numBlocks = getNumBlocks();
        setNumBlocks(numBlocks + 1);
        setNeuronId(endBlock, srcNeuronId);
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

    public void setNeuronId(int blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 4L, value);
    }

    public int getNeuronId(int blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 4L);
    }

    public void setSrcId(int blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 8L, value);
    }

    public int getSrcId(int blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 8L);
    }

    public void setSrcType(int blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 12L, value);
    }

    public int getSrcType(int blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 12L);
    }

    // --- Entry Header ---

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
