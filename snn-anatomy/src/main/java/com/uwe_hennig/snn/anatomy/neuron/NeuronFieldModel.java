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
/// │  40: numNeighbours    : int     │
/// │  44: attraction       : float   │
/// │ Field Neighbour Ids             │
/// │  48: field1: int                │
/// │  52: field2: int                │
/// │  56: field3: int                │
/// │  ...                            │
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

    final Arena         arena;
    final MemorySegment segment;
    final int           totalSize;

    final int maxEdges;
    final int fieldSize;

    final SnnLockSupport lockSupport;


    public NeuronFieldModel(int numFields, int maxEdges) {
        this.totalSize = calculateSize(numFields, maxEdges);
        this.arena = Arena.ofShared();

        this.segment = arena.allocate(totalSize);
        this.lockSupport = new SnnLockSupport(segment);
        this.maxEdges = maxEdges;
        this.fieldSize = FIELD_HEADER_SIZE + maxEdges * VALUE_SIZE;

        initMeta();
    }

    public NeuronFieldModel(Path filePath) throws IOException {
        this.arena = Arena.ofShared();
        try (FileChannel channel = FileChannel.open(filePath, StandardOpenOption.READ)) {
            this.totalSize = (int) channel.size();
            this.segment = arena.allocate(this.totalSize);
            channel.read(this.segment.asByteBuffer());
            this.lockSupport = new SnnLockSupport(segment);
            this.maxEdges = (super.getEntrySize() - FIELD_HEADER_SIZE) / 4;
            this.fieldSize = FIELD_HEADER_SIZE + maxEdges * VALUE_SIZE;
        }
    }

    private void initMeta() {
        setMagic(MAGIC);
        setVersion(1);
        setMetaSize(META_SIZE);
        setTotalSize(totalSize);
        setEndOffset(META_SIZE);

        setNumNeighbours(getEndOffset(), 0);
        setAttraction(getEndOffset(), 0.0f);
    }

    public static int calculateSize(int numFields, int numMaxEdges) {
        return META_SIZE + (FIELD_HEADER_SIZE +  numMaxEdges) * numMaxEdges;
    }

    @Override
    MemorySegment getSegment() {
        return segment;
    }

    public void close() {
        arena.close();
    }

    // ----- getter/setter -----

    int getFieldType(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE);
    }

    void setFieldType(int offset, int type) {
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE, type);
    }

    int getNumNeighbours(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 4L);
    }

    void setNumNeighbours(int offset, int value) {
        assert value >= 0 && value <= maxEdges : "invalid parameter in setNumNeighbours";
        segment.set(ValueLayout.JAVA_INT, offset + LOCK_SIZE + 4L, value);
    }

    float getAttraction(int offset) {
        return segment.get(ValueLayout.JAVA_FLOAT, offset + LOCK_SIZE + 8L);
    }

    void setAttraction(int offset, float value) {
        segment.set(ValueLayout.JAVA_FLOAT, offset + LOCK_SIZE + 8L, value);
    }


    int getNeighbourId(int offset, int position) {
        assert position >= 0 && position <= maxEdges : "invalid parameter in getFieldId";
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + position * 4L);
    }

    void setNeighbourId(int offset, int position, int identifier) {
        assert position >= 0 && position <= maxEdges : "invalid parameter in setFieldId";
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + position * 4L, identifier);
    }

    void addNeighbourId(int offset, int identifier) {
        int numNeighbours = getNumNeighbours(offset);
        assert numNeighbours < maxEdges : "invalid parameter in addNeighbourId";

        setNeighbourId(offset, numNeighbours, identifier);
        setNumNeighbours(offset, numNeighbours + 1);
    }

    int [] getNeighbours(int offset) {
        int numFields = getNumNeighbours(offset);
        int [] result = new int[numFields];
        MemorySegment.copy(segment, ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE, result, 0, numFields);
        return result;
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
        setEndOffset(newOffset + fieldSize);

        int entryCount = getEntryCount();
        setEntryCount(entryCount + 1);

        return newOffset;
    }


}
