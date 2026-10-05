/// @(#)ThresholdModel.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/// ThresholdModel
/// ```
/// ┌─────────────────────────────────┐
/// │ MetaInfo                        │
/// │  00: magic           : int      │
/// │  ...                            │
/// │  28: endOffset       : int      │
/// ├─────────────────────────────────┤
/// │ Entry 0                         │
/// │  32: threshold       : float    │
/// │  36: thresholdScale  : float    │
/// │  40: timeLimit       : float    │
/// ├─────────────────────────────────┤
/// │ Entry 1                         │
/// │  44: threshold       : float    │
/// │  48: thresholdScale  : float    │
/// │  52: timeLimit       : float    │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
/// ```
///
/// @author Uwe Hennig
///
public final class ThresholdModel extends MetaDataAccessor {
    static final int MAGIC      = ('T' << 24) | ('M' << 16) | ('_' << 8) | '1';
    static final int VALUE_SIZE = 4;
    static final int ENTRY_SIZE = 12;

    final Arena         arena;
    final MemorySegment segment;

    final int size;

    // ----- public -----

    public ThresholdModel(int size) {
        assert size >= META_SIZE + ENTRY_SIZE : "'size' must be able to contain at least one entry";

        this.size = size;
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(size);
        initMeta();
    }

    public ThresholdModel(Path filePath) throws IOException {
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

    float getThreshold(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 0L);
    }

    void setThreshold(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 0L, value);
    }

    float getThresholdScale(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 4L);
    }

    void setThresholdScale(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 4L, value);
    }

    float getTimeLimit(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 8L);
    }

    void setTimeLimit(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 8L, value);
    }
}
