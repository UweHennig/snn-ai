/// @(#)DendritModel.java
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

/// DendritModel
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
/// │  44: weightId        : int      │
/// │  48: somaId          : int      │
/// ├─────────────────────────────────┤
/// │ Entry 1                         │
/// │  52: lock            : int      │
/// │  56: fieldId         : int      │
/// │  60: neuronId        : int      │
/// │  64: weightId        : int      │
/// │  68: somaId          : int      │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
/// ````
///
/// @author Uwe Hennig
///
public final class DendritModel extends MetaDataAccessor {
    static final int MAGIC      = ('D' << 24) | ('E' << 16) | ('M' << 8) | '1';
    static final int VALUE_SIZE = 4;
    static final int ENTRY_SIZE = 20;
    static final int LOCK_SIZE  = 4;

    final Arena         arena;
    final MemorySegment segment;
    final int           totalSize;

    final SnnLockSupport lockSupport;

    // ----- public -----

    public DendritModel(int totalSize) {
        assert totalSize >= META_SIZE + ENTRY_SIZE : "'totalSize' must be able to contain at least one entry";

        this.totalSize = totalSize;
        this.arena = Arena.ofShared();

        this.segment = arena.allocate(totalSize);
        this.lockSupport = new SnnLockSupport(segment);

        initMeta();
    }

    public DendritModel(Path filePath) throws IOException {
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
        lockSupport.readUnlock(offset);;
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

    public int claimSlot(int fieldOffset, int neuronOffset, int somaOffset) {
        int newOffset = super.claimSlot();

        setFieldId(newOffset, fieldOffset);
        setNeuronId(newOffset, neuronOffset);
        setSomaId(newOffset, somaOffset);

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

    int getSomaId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L);
    }

    void setSomaId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L, value);
    }

    int getWeighId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 12L);
    }

    void setWeightId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 12L, value);
    }
}
