/// @(#)NeuronFieldModel.java
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

/// NeuronFieldModel
/// ```
/// ┌─────────────────────────────────┐
/// │ MetaInfo                        │
/// │  00: magic           : int      │
/// │  ...                            │
/// │  28: endOffset       : int      │
/// ├─────────────────────────────────┤
/// │ Entry 0                         │
/// │  32: lock             : int     │
/// │  36: type             : int     │
/// │  40: fieldId          : int     │
/// │  44: neuronRef        : int     │
/// │  48: outNeighboursRef : int     │
/// │  52: inNeighboursRef  : int     │
/// ├─────────────────────────────────┤
/// │ Entry 1                         │
/// │  56: lock             : int     │
/// │  60: type             : int     │
/// │  64: fieldId          : int     │
/// │  68: neuronRef        : int     │
/// │  72: outNeighboursRef : int     │
/// │  76: inNeighboursRef  : int     │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
/// ````
///
/// @author Uwe Hennig
///
public final class NeuronFieldModel extends MetaDataAccessor {
    static final int MAGIC      = ('N' << 24) | ('F' << 16) | ('M' << 8) | '1';
    static final int VALUE_SIZE = 4;
    static final int ENTRY_SIZE = 24;
    static final int LOCK_SIZE  = 4;

    final Arena         arena;
    final MemorySegment segment;
    final int           totalSize;

    final SnnLockSupport lockSupport;

    // ----- public -----

    public NeuronFieldModel(int totalSize) {
        assert totalSize >= META_SIZE + ENTRY_SIZE : "'totalSize' must be able to contain at least one entry";

        this.totalSize = totalSize;
        this.arena = Arena.ofShared();

        this.segment = arena.allocate(totalSize);
        this.lockSupport = new SnnLockSupport(segment);

        initMeta();
    }

    public NeuronFieldModel(Path filePath) throws IOException {
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

    public static int getEntryOffset(int entry) {
        return META_SIZE + ENTRY_SIZE * entry;
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

    @Override
    public int claimSlot() {
        int newOffset = super.claimSlot();
        setFieldId(newOffset, newOffset);
        return newOffset;
    }

    // ----- getter/setter -----

    int getType(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 0L);
    }

    void setType(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 0L, value);
    }

    int getFieldId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 4L);
    }

    void setFieldId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 0L, value);
    }

    int getNeuronRef(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L);
    }

    void setNeuronRef(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L, value);
    }

    int getOutNeighboursRef(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 12L);
    }

    void setOutNeighboursRef(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 12L, value);
    }

    int getInNeighbourRef(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 16L);
    }

    void setInNeighboursRef(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 16L, value);
    }
}
