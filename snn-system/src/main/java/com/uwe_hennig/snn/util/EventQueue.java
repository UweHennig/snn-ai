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
import java.lang.foreign.SequenceLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * EventQueue
 *
 * @author Uwe Hennig
 */
public class EventQueue {
    private final long queueCapacity;
    private final long mask;

    private final Arena         arena;
    private final MemorySegment controlSegment;
    private final MemorySegment eventSegment;

    // @formatter:off
    private static final GroupLayout CONTROL_LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("lock"),
        ValueLayout.JAVA_INT.withName("head"),
        ValueLayout.JAVA_INT.withName("tail"),
        MemoryLayout.paddingLayout(4)
    );

    private static final GroupLayout EVENT_LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("tapeId"),
        ValueLayout.JAVA_INT.withName("length")
    );

    private static final VarHandle VH_LOCK = CONTROL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lock"));
    private static final VarHandle VH_HEAD = CONTROL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("head"));
    private static final VarHandle VH_TAIL = CONTROL_LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("tail"));

    private final VarHandle VH_TAPE_ID;
    private final VarHandle VH_LENGTH;

    public EventQueue(long capacity) {
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException("The capacity must be a power of two!");
        }

        this.queueCapacity = capacity;
        this.mask = capacity - 1;
        this.arena = Arena.ofShared();

        this.controlSegment = arena.allocate(CONTROL_LAYOUT);

        SequenceLayout eventArrayLayout = MemoryLayout.sequenceLayout(capacity, EVENT_LAYOUT);
        this.eventSegment = arena.allocate(eventArrayLayout);

        this.VH_TAPE_ID = eventArrayLayout.varHandle(MemoryLayout.PathElement.sequenceElement(), MemoryLayout.PathElement.groupElement("tapeId"));
        this.VH_LENGTH = eventArrayLayout.varHandle( MemoryLayout.PathElement.sequenceElement(), MemoryLayout.PathElement.groupElement("length"));
    }
    // @formatter:on

    public boolean enqueue(int tapeId, int length) {
        lock();
        try {
            int head = (int) VH_HEAD.get(controlSegment, 0L);
            int tail = (int) VH_TAIL.get(controlSegment, 0L);

            if (tail - head == queueCapacity) {
                return false;
            }

            long index = tail & mask;

            VH_TAPE_ID.set(eventSegment, 0L, index, tapeId);
            VH_LENGTH.set(eventSegment, 0L, index, length);

            VH_TAIL.set(controlSegment, 0L, tail + 1);

            return true;
        } finally {
            unlock();
        }
    }

    public int[] dequeue() {
        lock();
        try {
            int head = (int) VH_HEAD.get(controlSegment, 0L);
            int tail = (int) VH_TAIL.get(controlSegment, 0L);

            if (head == tail) {
                return null;
            }

            long index = head & mask;

            int tapeId = (int) VH_TAPE_ID.get(eventSegment, 0L, index);
            int length = (int) VH_LENGTH.get(eventSegment, 0L, index);

            VH_HEAD.set(controlSegment, 0L, head + 1);

            return new int[] {tapeId, length};
        } finally {
            unlock();
        }
    }

    public void close() {
        if (arena != null) {
            arena.close();
        }
    }

    private void lock() {
        int spins = 0;
        while ((int) VH_LOCK.compareAndExchange(controlSegment, 0L, 0, 1) != 0) {
            if (spins < 64) {
                Thread.onSpinWait();
                spins++;
            }
        }
    }

    private void unlock() {
        VH_LOCK.setRelease(controlSegment, 0L, 0);
    }
}
