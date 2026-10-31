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
/// │ Field Header                    │
/// │  32: lock             : int     │
/// │  36: type             : int     │
/// │  40: attraction       : float   │
/// │  44: numFields        : int     │
/// │ Field Neighbour Ids             │
/// │  48: field1: int                │
/// │  52: field2: int                │
/// │  56: field3: int                │
/// │  60: field4: int                │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
/// ```
///
/// @author Uwe Hennig
///
public final class NeuronFieldModel extends MetaDataAccessor {
    static final int   MAGIC       = ('N' << 24) | ('F' << 16) | ('M' << 8) | '1';
    static final int   VALUE_SIZE  = 4;
    static final int   LOCK_SIZE   = 4;

    static final int FIELD_HEADER_SIZE = 3 * VALUE_SIZE + LOCK_SIZE;
    static final int FIELD_NEIGHBOURS  = 4;
    static final int FIELD_NEIGHBOURS_SIZE = FIELD_NEIGHBOURS * VALUE_SIZE;
    static final int FIELD_SIZE = FIELD_NEIGHBOURS_SIZE + FIELD_HEADER_SIZE;

    final Arena         arena;
    final MemorySegment segment;
    final int           totalSize;

    final SnnLockSupport lockSupport;

    public NeuronFieldModel(int size) {
        this.totalSize = size;
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

    private void initMeta() {
        setMagic(MAGIC);
        setVersion(1);
        setMetaSize(META_SIZE);
        setTotalSize(totalSize);
        setEndOffset(META_SIZE);
    }

    public static int calculateSize(int amountFields) {
        return (FIELD_HEADER_SIZE + FIELD_NEIGHBOURS_SIZE) * amountFields;
    }

    @Override
    MemorySegment getSegment() {
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
        lockSupport.readUnlock(offset);;
    }

    public int claimField() {
        int newOffset = getEndOffset();
        setEndOffset(newOffset + FIELD_SIZE);

        int entryCount = getEntryCount();
        setEntryCount(entryCount + 1);

        return newOffset;
    }

    // ----- getter/setter -----

    int getFieldType(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE);
    }

    void setFieldType(int offset, int type) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE, type);
    }

    float getAttraction(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + LOCK_SIZE + 4L);
    }

    void setAttraction(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + LOCK_SIZE + 4L, value);
    }

    int getNumNeighbours(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L);
    }

    void setNumNeighbours(int offset, int value) {
        assert value >= 0 && value <= 4 : "invalid parameter in setNumNeighbours";
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 8L, value);
    }

    int getFieldId(int offset, int position) {
        assert position >= 0 && position <= 4 : "invalid parameter in getFieldId";
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + position * 4L);
    }

    void setFieldId(int offset, int position, int identifier) {
        assert position >= 0 && position <= 4 : "invalid parameter in setFieldId";
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + position * 4L, identifier);
    }

    int [] getNeigbours(int offset) {
        int numFields = getNumNeighbours(offset);
        int [] result = new int[numFields];
        MemorySegment.copy(segment, ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE, result, 0, numFields);
        return result;
    }
}
