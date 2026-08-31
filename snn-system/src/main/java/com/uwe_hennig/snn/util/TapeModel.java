/**
 * @(#)TapeModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.invoke.VarHandle;

/**
 * TapeModel
 * The TapeModel is used to transfer runtime data for an event.
 *
 * @author Uwe Hennig
 */
public class TapeModel {
    // @formatter:off
    static final GroupLayout ELEMENT_LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("stimulusType"),
        JAVA_INT.withName("targetId"),
        JAVA_INT.withName("targetType"),
        JAVA_FLOAT.withName("value")
    ).withByteAlignment(8);

    static final GroupLayout CONTROL_LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("status"),
        JAVA_INT.withName("length")
    ).withByteAlignment(8);

    static final VarHandle VH_STATUS = CONTROL_LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("status"));
    static final VarHandle VH_LENGTH = CONTROL_LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("length"));

    static final VarHandle VH_STIMULUS_TYPE = ELEMENT_LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("stimulusType"));
    static final VarHandle VH_TARGET_ID     = ELEMENT_LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("targetId"));
    static final VarHandle VH_TARGET_TYPE   = ELEMENT_LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("targetType"));
    static final VarHandle VH_VALUE         = ELEMENT_LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("value"));
    // @formatter:on

    final Arena         arena;
    final MemorySegment statusSegment;
    final MemorySegment dataSegment;

    final int capacity; // Maximum number of entries per block
    final int blocks;   // Number of blocks (e.g. 3)

    public TapeModel(int capacity, int blocks) {
        this.capacity = capacity;
        this.blocks = blocks;
        this.arena = Arena.ofShared();

        this.statusSegment = arena.allocate(MemoryLayout.sequenceLayout(blocks, CONTROL_LAYOUT));
        this.dataSegment = arena.allocate(MemoryLayout.sequenceLayout((long) capacity * blocks, ELEMENT_LAYOUT));

        for (int i = 0; i < blocks; i++) {
            setCapacity(i, capacity);
        }
    }

    public int getStimulusType(int block, long index) {
        return (int) VH_STIMULUS_TYPE.get(dataSegment, 0L, globalIndex(block, index));
    }

    public void setStimulusType(int block, long index, int type) {
        VH_STIMULUS_TYPE.set(dataSegment, 0L, globalIndex(block, index), type);
    }

    public int getTargetId(int block, long index) {
        return (int) VH_TARGET_ID.get(dataSegment, 0L, globalIndex(block, index));
    }

    public void setTargetId(int block, long index, int targetId) {
        VH_TARGET_ID.set(dataSegment, 0L, globalIndex(block, index), targetId);
    }

    public int getTargetType(int block, long index) {
        return (int) VH_TARGET_TYPE.get(dataSegment, 0L, globalIndex(block, index));
    }

    public void setTargetType(int block, long index, int type) {
        VH_TARGET_TYPE.set(dataSegment, 0L, globalIndex(block, index), type);
    }

    public float getValue(int block, long index) {
        return (float) VH_VALUE.get(dataSegment, 0L, globalIndex(block, index));
    }

    public void setValue(int block, long index, float value) {
        VH_VALUE.set(dataSegment, 0L, globalIndex(block, index), value);
    }

    // --- Administration ---

    public int getStatus(int block) {
        return (int) VH_STATUS.get(statusSegment, 0L, (long) block);
    }

    public boolean setStatus(int block, int fromStatus, int toStatus) {
        int spins = 0;
        while (true) {
            if (VH_STATUS.compareAndSet(statusSegment, 0L, (long) block, fromStatus, toStatus)) {
                return true;
            }
            if (++spins > 64) {
                return false;
            }
            Thread.onSpinWait();
        }
    }

    public int getCapacity(int block) {
        return (int) VH_LENGTH.get(statusSegment, 0L, (long) block);
    }

    void setCapacity(int block, int length) {
        if (length < 0 || length > capacity) {
            throw new IllegalArgumentException("Länge überschreitet Kapazität");
        }
        VH_LENGTH.set(statusSegment, 0L, (long) block, length);
    }

    long globalIndex(int block, long index) {
        if (index < 0 || index >= capacity) {
            throw new IndexOutOfBoundsException("Index " + index + " beyond the block capacity " + capacity);
        }
        if (block < 0 || block >= blocks) {
            throw new IndexOutOfBoundsException("Block " + block + " does not exist.");
        }
        return (long) block * capacity + index;
    }

    public void close() {
        if (arena != null) {
            this.arena.close();
        }
    }
}