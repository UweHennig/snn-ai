/// @(#)PlasticityModel.java
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

/// PlasticityModel
/// ```
/// ┌─────────────────────────────────┐
/// │ MetaInfo                        │
/// │  00: magic           : int      │
/// │  ...                            │
/// │  28: endOffset       : int      │
/// ├─────────────────────────────────┤
/// │ Entry 0                         │
/// │    ===  DYNAMIC STATE  ===      │
/// │  32: currentPotential : float   │
/// │  36: lastUpdateTime   : float   │
/// │                                 │
/// │    === KNOWLEDGE STATE ===      │
/// │  40: targetPotential  : float   │
/// │  44: restingPotential : float   │
/// │                                 │
/// │    === TIME CONSTANTS  ===      │
/// │  48: targetTime       : float   │
/// │  52: restingTime      : float   │
/// │                                 │
/// │    === RATES ===                │
/// │  56: targetRate       : float   │
/// │  60: restingRate      : float   │
/// ├─────────────────────────────────┤
/// │ Entry 1                         │
/// │  64: currentPotential : float   │
/// │  68: lastUpdateTime   : float   │
/// │  72: targetPotential  : float   │
/// │  76: restingPotential : float   │
/// │  80: targetTime       : float   │
/// │  84: restingTime      : float   │
/// │  88: targetRate       : float   │
/// │  92: restingRate      : float   │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
/// ```
///
/// @author Uwe Hennig
///
public final class PlasticityModel extends MetaDataAccessor {
    static final int MAGIC      = ('D' << 24) | ('W' << 16) | ('_' << 8) | '1';
    static final int VALUE_SIZE = 4;
    static final int ENTRY_SIZE = 32;

    final Arena         arena;
    final MemorySegment segment;

    final int size;

    public PlasticityModel(int size) {
        assert size >= META_SIZE + ENTRY_SIZE : "'size' must be able to contain at least one entry";

        this.size = size;
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(size);
        initMeta();
    }

    public PlasticityModel(Path filePath) throws IOException {
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

    float getCurrentPotential(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 0L);
    }

    void setCurrentPotential(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 0L, value);
    }

    float getLastUpdateTime(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 4L);
    }

    void setLastUpdateTime(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 4L, value);
    }

    float getTargetPotential(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 8L);
    }

    void setTargetPotential(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 8L, value);
    }

    float getRestingPotential(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 12L);
    }

    void setRestingPotential(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 12L, value);
    }

    float getTargetTime(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 16L);
    }

    void setTargetTime(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 16L, value);
    }

    float getRestingTime(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 20L);
    }

    void setRestingTime(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 20L, value);
    }

    float getTargetRate(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 24L);
    }

    void setTargetRate(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 24L, value);
    }

    float getRestingRate(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + 28L);
    }

    void setRestingRate(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + 28L, value);
    }

}
