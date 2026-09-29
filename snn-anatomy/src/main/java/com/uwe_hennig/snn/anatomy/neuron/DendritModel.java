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

///
/// DendritModel
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
/// │  32: fieldId         : int      │
/// │  36: neuronId        : int      │
/// │  40: weightId        : int      │
/// │  44: somaId          : int      │
/// ├─────────────────────────────────┤
/// │ Entry 1                         │
/// │  48: lock            : int      │
/// │  52: fieldId         : int      │
/// │  56: neuronId        : int      │
/// │  60: weightId        : int      │
/// │  64: somaId          : int      │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
/// ````
///
/// @author Uwe Hennig
///
public final class DendritModel extends MetaDataAccessor {
    static final int MAGIC      = ('D' << 24) | ('M' << 16) | ('_' << 8) | '1';
    static final int VALUE_SIZE = 4;
    static final int META_SIZE  = 28;
    static final int ENTRY_SIZE = 20;

    final int           capacity;
    final Arena         arena;
    final MemorySegment segment;

    // ----- public -----

    public DendritModel(int capacity) {
        assert capacity >= META_SIZE + ENTRY_SIZE : "'capacity' must be able to contain at least one entry";

        this.capacity = capacity;
        this.arena = Arena.ofShared();

        this.segment = arena.allocate(capacity);
        initMeta();
    }

    public DendritModel(Path filePath) throws IOException {
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

    public int alloc(int fieldId, int neuronId, int somaId, int weightId) {
        int newOffset = getEndOffset();
        setEndOffset(newOffset + ENTRY_SIZE);

        setFieldId(newOffset, fieldId);
        setNeuronId(newOffset, neuronId);
        setWeightId(newOffset, weightId);
        setSomaId(newOffset, somaId);

        int numEntries = getNumEntries();
        setNumEntries(numEntries + 1);

        return newOffset;
    }

    // ----- getter/setter -----

    int getFiedlId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + 4L);
    }

    void setFieldId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + 4L, value);
    }

    int getNeuronId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + 8L);
    }

    void setNeuronId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + 8L, value);
    }

    int getSomaId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + 12L);
    }

    void setSomaId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + 12L, value);
    }

    int getWeighId(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + 16L);
    }

    void setWeightId(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + 16L, value);
    }
}
