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
    private final long queueCapacity;
    private final long mask;
    private final Arena arena;
    private final MemorySegment controlSegment;
    private final MemorySegment eventSegment;

    private static final long CACHE_LINE = 64;

    // @formatter:off
    // Das Control-Layout mit vollem Padding zur Vermeidung von False Sharing
    private static final GroupLayout CONTROL_LAYOUT = MemoryLayout.structLayout(
        MemoryLayout.paddingLayout(CACHE_LINE),
        ValueLayout.JAVA_INT.withName("lockPut"),
        MemoryLayout.paddingLayout(CACHE_LINE - 4),
        ValueLayout.JAVA_INT.withName("tail"),
        MemoryLayout.paddingLayout(CACHE_LINE - 4),
        ValueLayout.JAVA_INT.withName("lockTake"),
        MemoryLayout.paddingLayout(CACHE_LINE - 4),
        ValueLayout.JAVA_INT.withName("head"),
        MemoryLayout.paddingLayout(CACHE_LINE - 4)
    );

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

        this.eventSegment = arena.allocate(capacity * 8);
    }

    /**
     * Adds an event to the queue.
     * It only blocks other producers, not the consumers.
     */
    public boolean enqueue(int tapeId, int length) {
        lock(VH_LOCK_PUT);
        try {
            int head = (int) VH_HEAD.getAcquire(controlSegment, 0L);
            int tail = (int) VH_TAIL.get(controlSegment, 0L);

            if (tail - head == queueCapacity) {
                return false; // Queue full
            }

            long offset = (tail & mask) << 3;

            VH_INT.set(eventSegment, offset, tapeId);
            VH_INT.set(eventSegment, offset + 4, length);

            VH_TAIL.setRelease(controlSegment, 0L, tail + 1);
            return true;
        } finally {
            unlock(VH_LOCK_PUT);
        }
    }

    /**
     * Reads an event from the queue.
     * It only blocks other consumers, not the producers.
     */
    public int[] dequeue() {
        lock(VH_LOCK_TAKE);
        try {
            int head = (int) VH_HEAD.get(controlSegment, 0L);
            int tail = (int) VH_TAIL.getAcquire(controlSegment, 0L);

            if (head == tail) {
                return null;
            }

            long offset = (head & mask) << 3;

            int tId = (int) VH_INT.get(eventSegment, offset);
            int len = (int) VH_INT.get(eventSegment, offset + 4);

            VH_HEAD.setRelease(controlSegment, 0L, head + 1);

            return new int[] { tId, len };
        } finally {
            unlock(VH_LOCK_TAKE);
        }
    }

    // --- High Performance Locking ---

    private void lock(VarHandle lockHandle) {
        if ((int) lockHandle.compareAndExchange(controlSegment, 0L, 0, 1) == 0) {
            return;
        }
        while ((int) lockHandle.compareAndExchange(controlSegment, 0L, 0, 1) != 0) {
            Thread.onSpinWait();
        }
    }

    private void unlock(VarHandle lockHandle) {
        lockHandle.setRelease(controlSegment, 0L, 0);
    }

    @Override
    public void close() {
        if (arena != null) {
            arena.close();
        }
    }
}