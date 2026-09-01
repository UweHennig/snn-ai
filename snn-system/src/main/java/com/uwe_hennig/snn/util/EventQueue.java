/**
 * @(#)EventQueue.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * EventQueue
 *
 * @author Uwe Hennig
 */
public class EventQueue implements AutoCloseable {
    private final long          queueCapacity;
    private final long          mask;
    private final Arena         arena;
    private final MemorySegment controlSegment;
    private final MemorySegment eventSegment;

    // Hardware-Optimierung: Cache-Lines
    private static final long CACHE_LINE = 64;
    private static final long PADDING    = 56; // 64 - 8 (für Long)

    // Data optimisation: bit shifting instead of multiplication
    private static final int  EVENT_SHIFT = 3; // 2^3 = 8 Bytes pro Event
    private static final long EVENT_SIZE  = 8; // 8 Bytes (2 Ints)
    private static final long ALIGN_8     = 8; // 8-Byte Alignment for memory

    // @formatter:off
    private static final GroupLayout CONTROL_LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_LONG.withName("lockPut"),
        MemoryLayout.paddingLayout(PADDING),

        ValueLayout.JAVA_LONG.withName("tail"),
        MemoryLayout.paddingLayout(PADDING),

        ValueLayout.JAVA_LONG.withName("lockTake"),
        MemoryLayout.paddingLayout(PADDING),

        ValueLayout.JAVA_LONG.withName("head"),
        MemoryLayout.paddingLayout(PADDING)
    ).withByteAlignment(CACHE_LINE);

    private static final VarHandle VH_LOCK_PUT  = CONTROL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lockPut"));
    private static final VarHandle VH_LOCK_TAKE = CONTROL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lockTake"));
    private static final VarHandle VH_TAIL      = CONTROL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("tail"));
    private static final VarHandle VH_HEAD      = CONTROL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("head"));

    private static final VarHandle VH_INT = ValueLayout.JAVA_INT.varHandle();
    // @formatter:on

    public EventQueue(long capacity) {
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException("Capacity must be a power of two!");
        }
        this.queueCapacity = capacity;
        this.mask = capacity - 1;
        this.arena = Arena.ofShared();

        this.controlSegment = arena.allocate(CONTROL_LAYOUT);

        this.eventSegment = arena.allocate(capacity * EVENT_SIZE, ALIGN_8);
    }

    public boolean enqueue(int tapeId, int length) {
        lock(VH_LOCK_PUT);
        try {
            long head = (long) VH_HEAD.getAcquire(controlSegment, 0L);
            long tail = (long) VH_TAIL.get(controlSegment, 0L);

            if (tail - head == queueCapacity) {
                return false;
            }

            long offset = (tail & mask) << EVENT_SHIFT;

            VH_INT.set(eventSegment, offset, tapeId);
            VH_INT.set(eventSegment, offset + 4, length);

            VH_TAIL.setRelease(controlSegment, 0L, tail + 1);
            return true;
        } finally {
            unlock(VH_LOCK_PUT);
        }
    }

    public int[] dequeue() {
        lock(VH_LOCK_TAKE);
        try {
            long head = (long) VH_HEAD.get(controlSegment, 0L);
            long tail = (long) VH_TAIL.getAcquire(controlSegment, 0L);

            if (head == tail) {
                return null;
            }

            long offset = (head & mask) << EVENT_SHIFT;

            int tId = (int) VH_INT.get(eventSegment, offset);
            int len = (int) VH_INT.get(eventSegment, offset + 4);

            VH_HEAD.setRelease(controlSegment, 0L, head + 1);

            return new int[] { tId, len };
        } finally {
            unlock(VH_LOCK_TAKE);
        }
    }

    private void lock(VarHandle lockHandle) {
        if ((long) lockHandle.compareAndExchange(controlSegment, 0L, 0L, 1L) == 0L) {
            return;
        }
        while ((long) lockHandle.compareAndExchange(controlSegment, 0L, 0L, 1L) != 0L) {
            Thread.onSpinWait();
        }
    }

    private void unlock(VarHandle lockHandle) {
        lockHandle.setRelease(controlSegment, 0L, 0L);
    }

    @Override
    public void close() {
        arena.close();
    }
}