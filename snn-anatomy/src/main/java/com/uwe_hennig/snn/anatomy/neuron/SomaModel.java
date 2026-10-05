/// * @(#)SomaModel.java
/// * Copyright (c) 2026 Uwe Hennig
/// * All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/// SomaModel
/// ```
/// ┌─────────────────────────────────┐
/// │ MetaInfo                        │
/// │  00: magic           : int      │
/// │  ...                            │
/// │  28: endOffset       : int      │
/// ├─────────────────────────────────┤
/// │ Entry 0                         │
/// │  32: lock            : int      │
/// │  36: fieldId         : int      │
/// │  40: neuronId        : int      │
/// │  44: axonId          : int      │
/// │  48: potentialId     : int      │
/// │  52: thresholdId     : int      │
/// │  56: stpId           : int      │
/// │  60: ltpId           : int      │
/// ├─────────────────────────────────┤
/// │ Entry 1                         │
/// │  64: lock            : int      │
/// │  68: fieldId         : int      │
/// │  72: neuronId        : int      │
/// │  76: axonId          : int      │
/// │  80: potentialId     : int      │
/// │  84: thresholdId     : int      │
/// │  88: stpId           : int      │
/// │  92: ltpId           : int      │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
/// ````
///
/// @author Uwe Hennig
public final class SomaModel extends MetaDataAccessor {
    static final int MAGIC      = ('S' << 24) | ('O' << 16) | ('M' << 8) | '1';
    static final int VALUE_SIZE = 4;
    static final int LOCK_SIZE  = 4;
    static final int ENTRY_SIZE = 32;

    final Arena         arena;
    final MemorySegment segment;
    final int           totalSize;

    final SnnLockSupport lockSupport;

    public SomaModel(int totalSize) {
        assert totalSize >= META_SIZE + ENTRY_SIZE : "'totalSize' must be able to contain at least one entry";

        this.totalSize = totalSize;
        this.arena = Arena.ofShared();

        this.segment = arena.allocate(totalSize);
        this.lockSupport = new SnnLockSupport(segment);

        initMeta();
    }

    public SomaModel(Path filePath) throws IOException {
        this.arena = Arena.ofShared();
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            this.totalSize = (int) channel.size();
            this.segment = arena.allocate(this.totalSize);
            channel.read(this.segment.asByteBuffer());
            this.lockSupport = new SnnLockSupport(segment);
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
        arena.close();
    }

    // ----- lock -----
    void writeLock(int offset) {
        lockSupport.writeLock(offset);
    }

    void readLock(int offset) {
        lockSupport.readLock(offset);
    }

    void writeUnlock(int offset) {
        lockSupport.writeUnlock(offset);
    }

    void readUnlock(int offset) {
        lockSupport.readUnlock(offset);
        ;
    }

    // ----- meta -----

    void initMeta() {
        setMagic(MAGIC);
        setVersion(1);
        setMetaSize(META_SIZE);
        setEntrySize(ENTRY_SIZE);
        setTotalSize(totalSize);
        setMaxEntries((totalSize - META_SIZE) / ENTRY_SIZE);
        setEntryCount(0);

        // Data begins after the meta section
        setEndOffset(META_SIZE);
    }

    public int alloc(int fieldId, int neuronId, int axonId) {
        int newOffset = super.alloc();

        setFieldId(newOffset, fieldId);
        setNeuronId(newOffset, neuronId);
        setAxonId(newOffset, axonId);

        return newOffset;
    }

    // ----- getter/setter -----

    int getFieldId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 0L);
    }

    void setFieldId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 0L, value);
    }

    int getNeuronId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 4L);
    }

    void setNeuronId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 4L, value);
    }

    int getAxonId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L);
    }

    void setAxonId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L, value);
    }

    int getPotentialId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 12L);
    }

    void setPotentialId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 12L, value);
    }

    int getThresholdId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 16L);
    }

    void setThresholdId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 16L, value);
    }

    int getStpId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 20L);
    }

    void setStpId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 20L, value);
    }

    int getLtpId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 24L);
    }

    void setLtpId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 24L, value);
    }
}
