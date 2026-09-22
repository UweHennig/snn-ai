/// @(#)BufferedTransferSegment.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.util;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;

/// BufferedTransferSegment
/// ````
/// ┌────────────────────────────────┐
/// │ MetaInfo  (20 bytes)           │
/// │  00: numBlocks     : int       │
/// │  04: startOffset   : int       │
/// │  08: blockSize     : int       │
/// │  12: entrySize     : int       │
/// │  16: endOffset     : int       │
/// ├────────────────────────────────┤
/// │ Block Header (12 bytes)        │
/// │  20: srcId          : int      │
/// │  24: srcType        : int      │
/// │  28: entries        : int      │
/// ├────────────────────────────────┤
/// │ Entry 0 (56 bytes)             │
/// │  32: targetId      : int       │
/// │  36: targetType    : int       │
/// | Stimulus                       │
/// │  40: stim_status   : int       │
/// │  44: stim_in       : float     │
/// │  48: stim_wait     : float     │
/// │  52: stim_out      : float     │
/// | Stimulus Feedback Time         │
/// │  56: time_fb_status: int       │
/// │  60: time_fb_in    : float     │
/// │  64: time_fb_wait  : float     │
/// │  68: time_fb_out   : float     │
/// | Stimulus Feedback Value        │
/// │  72: val_fb_status : int       │
/// │  76: val_fb_in     : float     │
/// │  80: val_fb_wai t  : float     │
/// │  84: val_fb_out    : float     │
/// ├────────────────────────────────┤
/// │ Entry 1 (56 bytes)             │
/// │   88: targetId      : int      │
/// │   92: ....                     │
/// ├────────────────────────────────┤
/// │ ...                            │
/// ├────────────────────────────────┤
/// │ next Block                     │
/// └────────────────────────────────┘
///
/// All neuron elements have their own mini-queue for each stimulus
/// A feedback loop can have its own list of start neuron
/// elements. These transmit the stimuli via their graph structure.
/// ````
///
/// @author Uwe Hennig
public final class BufferedTransferSegment {
    static final float EMTPY_VALUE = Float.NaN;

    static final int META_SIZE         = 20;
    static final int BLOCK_HEADER_SIZE = 12;

    static final int ENTRY_HEADER_SIZE =  8;
    static final int QUEUE_SIZE        = 32;
    static final int ENTRY_SIZE        = ENTRY_HEADER_SIZE + QUEUE_SIZE * 3;

    final Arena         arena;
    final MemorySegment segment;

    public BufferedTransferSegment(int size) {
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(size);
        initMeta();
    }

    // --- META ---

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

    void setEndOffset(int value) {
        segment.set(ValueLayout.JAVA_INT, 16L, value);
    }

    int getEndOffset() {
        return segment.get(ValueLayout.JAVA_INT, 16);
    }

    private void initMeta() {
        setNumBlocks(0);
        setStartOffset(META_SIZE);
        setBlockHeaderSize(BLOCK_HEADER_SIZE);
        setEntrySize(ENTRY_SIZE);
        setEndOffset(META_SIZE);
    }

    // --- Block ---

    // returns the offset, which ist an index of receptor view
    public int allocateBlock(int entries, int srcId, int srcType) {
        int endBlock = getEndOffset();
        setEndOffset(endBlock + BLOCK_HEADER_SIZE + entries * ENTRY_SIZE);
        int numBlocks = getNumBlocks();
        setNumBlocks(numBlocks + 1);

        setSrcId(endBlock, srcId);
        setSrcType(endBlock, srcType);
        setEntries(endBlock, entries);

        return endBlock;
    }


    public void setSrcId(int blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 4, value);
    }

    public int getSrcId(int blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 4);
    }

    public void setSrcType(int blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 8, value);
    }

    public int getSrcType(int blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 8);
    }

    void setEntries(int blockOffset, int value) {
        segment.set(ValueLayout.JAVA_INT, blockOffset + 12, value);
    }

    public int getEntries(int blockOffset) {
        return segment.get(ValueLayout.JAVA_INT, blockOffset + 12);
    }

    // --- Enty Header ---


    public void setTrgId(int blockOffset, int entry, int value) {
        int offset = blockOffset + BLOCK_HEADER_SIZE + entry * ENTRY_SIZE;
        segment.set(ValueLayout.JAVA_INT, offset, value);
    }

    public int getTrgId(int blockOffset, int entry) {
        int offset = blockOffset + BLOCK_HEADER_SIZE + entry * ENTRY_SIZE;
        return segment.get(ValueLayout.JAVA_INT, offset);
    }

    public void setTrgType(int blockOffset, int entry, int value) {
        int offset = blockOffset + BLOCK_HEADER_SIZE + entry * ENTRY_SIZE + 4;
        segment.set(ValueLayout.JAVA_INT, offset, value);
    }

    public int getTrgType(int blockOffset, int entry) {
        int offset = blockOffset + BLOCK_HEADER_SIZE + entry * ENTRY_SIZE + 4;
        return segment.get(ValueLayout.JAVA_INT, offset);
    }

    // --- Queues ---

    public boolean offerStimulus(int blockOffset, int entry, float value) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_HEADER_SIZE + entry * ENTRY_SIZE;
        return BufferedTransferHelper.offer(segment, queueOffset, value);
    }

    public float pollStimulus(int blockOffset, int entry) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_HEADER_SIZE+ entry * ENTRY_SIZE;
        return BufferedTransferHelper.poll(segment, queueOffset);
    }

    public boolean offerFbTime(int blockOffset, int entry, float value) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_HEADER_SIZE + QUEUE_SIZE + entry * ENTRY_SIZE;
        return BufferedTransferHelper.offer(segment, queueOffset, value);
    }

    public float pollFbTime(int blockOffset, int entry) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_HEADER_SIZE + QUEUE_SIZE + entry * ENTRY_SIZE;
        return BufferedTransferHelper.poll(segment, queueOffset);
    }

    public boolean offerFbValue(int blockOffset, int entry, float value) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_HEADER_SIZE + 2 * QUEUE_SIZE + entry * ENTRY_SIZE;
        return BufferedTransferHelper.offer(segment, queueOffset, value);
    }

    public float pollFbValue(int blockOffset, int entry) {
        int queueOffset = blockOffset + BLOCK_HEADER_SIZE + ENTRY_HEADER_SIZE + 2 * QUEUE_SIZE + entry * ENTRY_SIZE;
        return BufferedTransferHelper.poll(segment, queueOffset);
    }

    public void close() {
        if (arena != null) {
            arena.close();
        }
    }
}
