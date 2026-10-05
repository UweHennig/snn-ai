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
/// │  ...                            │
/// │  28: endOffset       : int      │
/// ├─────────────────────────────────┤
/// │ Entry 0                         │
/// │  32: weight          : float    │
/// │  46: preSynapticTime : float    │
/// │  40: postSynapticTime: float    │
/// │  44: hebbTimeRange   : float    │
/// │  48: weightScale     : float    │
/// │  52: hebbScale       : float    │
/// │  56: timeLimit       : float    │
/// ├─────────────────────────────────┤
/// │ Entry 1                         │
/// │  60: weight          : float    │
/// │  64: preSynapticTime : float    │
/// │  68: postSynapticTime: float    │
/// │  72: hebbTimeRange   : float    │
/// │  76: weightScale     : float    │
/// │  80: hebbScale       : float    │
/// │  84: timeLimit       : float    │
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
    static final int ENTRY_SIZE = 28;

    final static long TIMEOUT_NS = 200_000;

    final Arena         arena;
    final MemorySegment segment;

    final int           size;

    // ----- public -----

    public WeightModel(int size) {
        assert size >= META_SIZE + ENTRY_SIZE : "'size' must be able to contain at least one entry";

        this.size = size;
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(size);
        initMeta();
    }

    public WeightModel(Path filePath) throws IOException {
        this.arena = Arena.ofShared();
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            this.size = (int) channel.size();
            this.segment = arena.allocate(this.size);
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
        setEntrySize(ENTRY_SIZE);
        setTotalSize(size);
        setMaxEntries((size - META_SIZE) / ENTRY_SIZE);
        setEntryCount(0);
        setEndOffset(META_SIZE);
    }

    // ----- getter/setter -----

    float getWeight(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 0L);
    }

    void setWeight(int offset, float weight) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 0L, weight);
    }

    float getPreSynapticTime(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 4L);
    }

    void setPreSynapticTime(int offset, float timestamp) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 4L, timestamp);
    }

    float getPostSynapticTime(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 8L);
    }

    void setPostSynapticTime(int offset, float timestamp) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 8L, timestamp);
    }

    float getHebbTimeRange(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 12L);
    }

    void setHebbTimeRange(int offset, float time) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 12L, time);
    }

    float getWeightScale(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 16L);
    }

    void setWeightScale(int offset, float timeRange) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 16L, timeRange);
    }

    float getHebbScale(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 20L);
    }

    void setHebbScale(int offset, float timeRange) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 20L, timeRange);
    }

    float getTimeLimit(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 24L);
    }

    void setTimeLimit(int offset, float timeRange) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 24L, timeRange);
    }
}
