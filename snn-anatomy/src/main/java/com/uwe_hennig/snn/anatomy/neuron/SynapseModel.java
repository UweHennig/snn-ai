/// @(#)SynapseModel.java
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

/// SynapseModel
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
/// │  44: targetId        : int      │
/// │  48: targetType      : int      │
/// │  52: modulatorId     : int      │
/// ├─────────────────────────────────┤
/// │ Entry 1                         │
/// │  56: lock            : int      │
/// │  60: fieldId         : int      │
/// │  64: neuronId        : int      │
/// │  68: targetId        : int      │
/// │  72: targetId        : int      │
/// │  74: modulatorId     : int      │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
/// ```
///
/// @author Uwe Hennig
///
public final class SynapseModel extends MetaDataAccessor {
    static final int MAGIC      = ('S' << 24) | ('Y' << 16) | ('M' << 8) | '1';
    static final int VALUE_SIZE = 4;
    static final int ENTRY_SIZE = 24;
    static final int LOCK_SIZE  = 4;

    final Arena         arena;
    final MemorySegment segment;
    final int           totalSize;

    final SnnLockSupport lockSupport;

    // ----- public -----

    public SynapseModel(int totalSize) {
        assert totalSize >= META_SIZE + ENTRY_SIZE : "'totalSize' must be able to contain at least one entry";

        this.totalSize = totalSize;
        this.arena = Arena.ofShared();

        this.segment = arena.allocate(totalSize);
        this.lockSupport = new SnnLockSupport(segment);

        initMeta();
    }

    public SynapseModel(Path filePath) throws IOException {
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

    // ----- lock/unlock -----

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

    public int claimSlot(int fieldId, int neuronId, int targetId) {
        int newOffset = super.claimSlot();

        setFieldId(newOffset, fieldId);
        setNeuronId(newOffset, neuronId);
        setTargetId(newOffset, targetId);

        return newOffset;
    }

    // ----- getter/setter -----

    int getFiedlId(int offset) {
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

    int getTargetId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L);
    }

    void setTargetId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L, value);
    }

    int getTargetType(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 12L);
    }

    void setTargetType(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 12L, value);
    }

    int getModulatorId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 16L);
    }

    void setModulatorId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 16L, value);
    }
}
