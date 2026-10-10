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
/// │  32: lock              : int    │
/// │  36: numNeurons        : int    │
/// │  40: maxNeurons        : int    │
/// │  44: numInNeighbors    : int    │
/// │  48: maxInNeighbors    : int    │
/// │  52: numOutNeighbors   : int    │
/// │  56: maxOutNeighbors   : int    │
/// │  60: numData           : int    │
/// │  64: maxData           : int    │
/// ├─────────────────────────────────┤
/// │ Neuron Is                       │
/// │  N_START: neuronId     : int    │
/// │ In Field Ids                    │
/// │  I_START: inNeighbourId: int    │
/// │ Out Field Ids                   │
/// │  O_START: outNeighbourIds: int  │
/// │ Field Data                      │
/// │  D_START: data           : int  │
/// ├─────────────────────────────────┤
/// │ ...                             │
/// └─────────────────────────────────┘
///
/// 32: numNeurons (int)        – Current number of neurons
/// 36: maxNeurons (int)        – Maximum capacity for neurons (important for neurogenesis)
/// 40: numInNeighbors (int)    – Current number of incoming neighbours
/// 44: maxInNeighbors (int)    – Fixed maximum capacity for in-neighbours
/// 48: numOutNeighbors (int)   – Current number of outgoing neighbours
/// 52: maxOutNeighbors (int)   – Fixed maximum capacity for out-neighbours
/// 56: numData (int)           – Current size of field data (APP)
/// 60: maxData (int)           – Maximum capacity of field data
/// 64: lock (int)              – Lock for neurogenesis
///
/// Field-Data (from Byte 64):
/// --------------------------------------------------
/// - N_START = 68
/// - N_END   = N_START + (maxNeurons * 4)       // Exclusive Boundary / Start In-Neighbours
/// --------------------------------------------------
/// - I_START = N_END                            // Fits seamlessly
/// - I_END   = I_START + (maxInNeighbors * 4)   // Exclusive Boundary / Start Out Neighbours
/// --------------------------------------------------
/// - O_START = I_END                            // Fits seamlessly
/// - O_END   = O_START + (maxOutNeighbors * 4)  // Exclusive boundary / Start field data
/// --------------------------------------------------
/// - D_START = O_END                            // Fits seamlessly
/// - D_END   = D_START + maxData                // End of the entire field / Total field size
/// ```
///
/// @author Uwe Hennig
///
public final class NeuronFieldModel extends MetaDataAccessor {
    static final int   MAGIC       = ('N' << 24) | ('F' << 16) | ('M' << 8) | '1';
    static final int   VALUE_SIZE  = 4;
    static final int   LOCK_SIZE   = 4;
    static final int   EMTPY_VALUE = Integer.MIN_VALUE;
    static final int[] EMTPY_ARRAY = new int[0];

    static final int FIELD_HEADER_SIZE = 8 * VALUE_SIZE + LOCK_SIZE;

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

    public static int calculateSize(int maxNeurons, int maxOutNeighbours, int maxInNeighbours, int maxData) {
        return META_SIZE + FIELD_HEADER_SIZE + (maxNeurons + maxOutNeighbours + maxInNeighbours + maxData + 1) * VALUE_SIZE;
    }

    @Override
    MemorySegment getSegment() {
        return segment;
    }

    public int claimField(int maxNeurons, int maxOutNeighbours, int maxInNeighbours, int maxData) {
        int range = calculateSize(maxNeurons, maxOutNeighbours, maxInNeighbours, maxData);
        int newOffset = getEndOffset();
        setEndOffset(newOffset + range);

        int entryCount = getEntryCount();
        setEntryCount(entryCount + 1);

        setNumNeurons(newOffset, 0);
        setMaxNeurons(newOffset, maxNeurons);
        setNumInNeighbours(newOffset, 0);
        setMaxInNeighbours(newOffset, maxInNeighbours);
        setNumData(newOffset, 0);
        setMaxData(newOffset, maxData);

        return newOffset;
    }

    public void addNeuron(int fieldOffset, int neuronId) {
        int numNeurons = getNumNeurons(fieldOffset);
        int maxNeurons = getMaxNeurons(fieldOffset);
        assert numNeurons + 1 <= maxNeurons : "OutOfBounds neurons";

        int relOffset = numNeurons * VALUE_SIZE;
        segment.set(ValueLayout.JAVA_INT, fieldOffset + FIELD_HEADER_SIZE + relOffset, neuronId);

        setNumNeurons(fieldOffset, numNeurons + 1);
    }

    public void addInField(int fieldOffset, int fieldId) {
        int numFields = getNumInNeighbours(fieldOffset);
        int maxFields = getMaxInNeighbours(fieldOffset);
        int maxNeurons = getMaxNeurons(fieldOffset);
        assert numFields + 1 <= maxFields : "OutOfBounds fields";

        int relOffset = (maxNeurons + numFields) * VALUE_SIZE;
        segment.set(ValueLayout.JAVA_INT, fieldOffset + FIELD_HEADER_SIZE + relOffset, fieldId);

        setNumInNeighbours(fieldOffset, numFields + 1);
    }

    public void addOutField(int fieldOffset, int fieldId) {
        int maxNeurons = getMaxNeurons(fieldOffset);
        int maxInFields = getMaxInNeighbours(fieldOffset);
        int maxOutFields = getMaxOutNeighbours(fieldOffset);
        int numOutFields = getNumOutNeighbours(fieldOffset);
        assert numOutFields + 1 <= maxOutFields : "OutOfBounds fields";

        int relOffset = (maxNeurons + maxInFields + numOutFields) * VALUE_SIZE;
        segment.set(ValueLayout.JAVA_INT, fieldOffset + FIELD_HEADER_SIZE + relOffset, fieldId);

        setNumInNeighbours(fieldOffset, numOutFields + 1);
    }


    int[] getFieldOffsets() {
        int numFields = getEntryCount();
        int currentOffset = META_SIZE;
        int[] result = new int[numFields];

        for (int i = 0; i < numFields; i++) {
            result[i] = currentOffset;
            currentOffset += calculateSize(
                getMaxNeurons(currentOffset),
                getMaxInNeighbours(currentOffset),
                getMaxOutNeighbours(currentOffset),
                getMaxData(currentOffset));
        }

        return result;
    }

    int[] getNeuronIds(int offset) {
        int entries = getNumNeurons(offset);

        int[] result = EMTPY_ARRAY;
        if (entries > 0) {
            result = new int[entries];
            long srcOffset = offset + FIELD_HEADER_SIZE;
            MemorySegment.copy(segment, ValueLayout.JAVA_INT, srcOffset, result, 0, entries);
        }

        return result;
    }

    int [] getInFields(int offset) {
        int entries = getNumInNeighbours(offset);
        int[] result = EMTPY_ARRAY;

        if (entries > 0) {
            int maxNeurons = getMaxNeurons(offset);
            result = new int[entries];
            long srcOffset = offset + FIELD_HEADER_SIZE + maxNeurons * VALUE_SIZE;
            MemorySegment.copy(segment, ValueLayout.JAVA_INT, srcOffset, result, 0, entries);
        }

        return result;
    }

    int [] getOutFields(int offset) {
        int entries = getNumOutNeighbours(offset);
        int [] result = EMTPY_ARRAY;

        if (entries > 0) {
            int maxNeurons = getMaxNeurons(offset);
            int maxInFieds = getMaxInNeighbours(offset);
            long srcOffset = offset + FIELD_HEADER_SIZE + (maxNeurons + maxInFieds) * VALUE_SIZE;
            MemorySegment.copy(segment, ValueLayout.JAVA_INT, srcOffset, result, 0, entries);
        }

        return result;
    }

    void setNumNeurons(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 0L, value);
    }

    int getNumNeurons(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 0L);
    }

    void setMaxNeurons(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 4L, value);
    }

    int getMaxNeurons(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 4L);
    }

    void setNumInNeighbours(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 8L, value);
    }

    int getNumInNeighbours(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 8L);
    }

    void setMaxInNeighbours(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 12L, value);
    }

    int getMaxInNeighbours(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 12L);
    }

    void setNumOutNeighbours(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 16L, value);
    }

    int getNumOutNeighbours(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 16L);
    }

    void setMaxOutNeighbours(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 20L, value);
    }

    int getMaxOutNeighbours(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 20L);
    }

    void setNumData(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 24L, value);
    }

    int getNumData(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 24L);
    }

    void setMaxData(int offset, int value) {
        segment.set(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 28L, value);
    }

    int getMaxData(int offset) {
        return segment.get(ValueLayout.JAVA_INT, offset + FIELD_HEADER_SIZE + 28L);
    }

    public void close() {
        arena.close();
    }
}
