/**
 * @(#)WeightModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/// WeightModel
/// ```
/// ┌─────────────────────────────────┐
/// │ MetaInfo                        │
/// │  00: magic           : int      │
/// │  04: version         : int      │
/// │  08: metaSize        : int      │
/// │  12: capacity        : int      │
/// │  16: entrySize       : int      │
/// │  20: numEntries      : int      │
/// │  24: endOffset       : int      │
/// ├─────────────────────────────────┤
/// │ Entry 0                         │
/// │  28: lock            : int      │
/// │  32: weight          : float    │
/// │  36: preSynapticTime : float    │
/// │  40: postSynapticTime: float    │
/// │  44: hebbTimeRange   : float    │
/// │  48: weightScale     : float    │
/// │  52: hebbScale       : float    │
/// │  56: timeLimit       : float    │
/// ├─────────────────────────────────┤
/// │ Entry 1                         │
/// │  60: lock            : int      │
/// │  64: weight          : float    │
/// │  68: preSynapticTime : float    │
/// │  72: postSynapticTime: float    │
/// │  76: hebbTimeRange   : float    │
/// │  80: weightScale     : float    │
/// │  84: hebbScale       : float    │
/// │  88: timeLimit       : float    │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
/// ```
///
/// @author Uwe Hennig
///
public final class WeightModel extends MetaDataAccessor {
    static final int MAGIC      = ('D' << 24) | ('W' << 16) | ('_' << 8) | '1';
    static final int VALUE_SIZE = 4;
    static final int META_SIZE  = 28;
    static final int ENTRY_SIZE = 64;

    final static long TIMEOUT_NS = 200_000;

    final int           capacity;
    final Arena         arena;
    final MemorySegment segment;

    // ----- public -----

    public WeightModel(int capacity) {
        assert capacity >= META_SIZE + ENTRY_SIZE : "'capacity' must be able to contain at least one entry";

        this.capacity = capacity;
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(capacity);
        initMeta();
    }

    public WeightModel(Path filePath) throws IOException {
        this.arena = Arena.ofShared();
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            this.capacity = (int) channel.size();
            this.segment = arena.allocate(this.capacity);
            channel.read(this.segment.asByteBuffer());
        }
    }

    public static int calculateSize(int entries) {
        return META_SIZE + ENTRY_SIZE * entries;
    }

    @Override
    protected MemorySegment getSegment() {
        return segment;
    }

    public void close() {
        if (arena != null) {
            arena.close();
        }
    }

    // ----- meta -----

    void initMeta() {
        setMagic(MAGIC);
        setVersion(1);
        setMetaSize(META_SIZE);
        setCapacity(capacity);
        setEntrySize(ENTRY_SIZE);
        setNumEntries(0);
        setEndOffset(META_SIZE);
    }

    public int alloc() {
        int newOffset = getEndOffset();
        setEndOffset(newOffset + ENTRY_SIZE);

        int numEntries = getNumEntries();
        setNumEntries(numEntries + 1);

        return newOffset;
    }

    // ----- getter/setter -----

    float getWeight(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 4L);
    }

    void setWeight(int offset, float weight) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 4L, weight);
    }

    float getPreSynapticTime(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 8L);
    }

    void setPreSynapticTime(int offset, float timestamp) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 8L, timestamp);
    }

    float getPostSynapticTime(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 12L);
    }

    void setPostSynapticTime(int offset, float timestamp) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 12L, timestamp);
    }

    float getHebbTimeRange(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 16L);
    }

    void setHebbTimeRange(int offset, float time) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 16L, time);
    }

    float getWeightScale(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 20L);
    }

    void setWeightScale(int offset, float timeRange) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 20L, timeRange);
    }

    float getHebbScale(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 24L);
    }

    void setHebbScale(int offset, float timeRange) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 24L, timeRange);
    }

    float getTimeLimit(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 28L);
    }

    void setTimeLimit(int offset, float timeRange) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 28L, timeRange);
    }
}
