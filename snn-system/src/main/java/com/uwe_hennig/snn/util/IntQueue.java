/**
 * @(#)IntQueue.java
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
import java.util.concurrent.locks.LockSupport;

/**
 * IntQueue
 * is a FIFO integer queue implemented with arena off heap. The queue is designed for positive integers.
 * TODO  Here we a  "Check-then-Act" problem!
 * @author Uwe Hennig
 */
public class IntQueue {
    private final long queueCapacity;
    private final long mask;

    private final Arena         arena;
    private final MemorySegment queuePtr;
    private final MemorySegment queueSegment;

    private static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("lock"),
        ValueLayout.JAVA_INT.withName("head"),
        ValueLayout.JAVA_INT.withName("tail"),
        MemoryLayout.paddingLayout(4)
    );

    private static final VarHandle VH_LOCK = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lock"));
    private static final VarHandle VH_HEAD = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("head"));
    private static final VarHandle VH_TAIL = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("tail"));

    private final VarHandle elementHandle;

    /**
     * Creates a new IntQueue with dynamic capacity.
     *
     * @param capacity The desired capacity (MUST be a power of two, e.g., 1024, 4096, 65536).
     */
    public IntQueue(long capacity) {
        if ((capacity & (capacity - 1)) != 0) {
            throw new IllegalArgumentException("The capacity must be a power of two!");
        }

        this.queueCapacity = capacity;
        this.mask = capacity - 1;

        this.arena = Arena.ofShared();
        this.queuePtr = arena.allocate(LAYOUT);

        SequenceLayout innerLayout = MemoryLayout.sequenceLayout(queueCapacity, ValueLayout.JAVA_INT);
        SequenceLayout fullSequenceLayout = MemoryLayout.sequenceLayout(1, innerLayout);

        this.queueSegment = arena.allocate(fullSequenceLayout);

        this.elementHandle = fullSequenceLayout.varHandle(MemoryLayout.PathElement.sequenceElement(), MemoryLayout.PathElement.sequenceElement());
    }

    /**
     * Closes the arena and frees up all off-heap memory.
     */
    public void close() {
        if (arena != null) {
            this.arena.close();
        }
    }

    /**
     * Adaptive Spin-Lock (A hybrid approach for minimal latency and reduced CPU load)
     */
    void lock() {
        int spins = 0;
        while (!VH_LOCK.compareAndSet(queuePtr, 0L, 0, 1)) {
            if (spins < 64) {
                Thread.onSpinWait(); // Phase 1: Fast CPU spinning
                spins++;
            } else {
                LockSupport.parkNanos(1); // Phase 2: OS thread parking during congestion
            }
        }
    }

    void unlock() {
        VH_LOCK.setRelease(queuePtr, 0L, 0);
    }

    /**
     * Inserts an element at the end of the FIFO queue.
     */
    public boolean offer(int value) {
        lock();
        try {
            int head = (int) VH_HEAD.get(queuePtr, 0L);
            int tail = (int) VH_TAIL.get(queuePtr, 0L);

            if (tail - head == queueCapacity) {
                return false;
            }

            long arrayIndex = tail & mask;
            elementHandle.set(this.queueSegment, 0L, 0L, arrayIndex, value);

            VH_TAIL.set(queuePtr, 0L, tail + 1);
            return true;
        } finally {
            unlock();
        }
    }

    public boolean isEmpty() {
        lock();
        try {
            int head = (int) VH_HEAD.get(queuePtr, 0L);
            int tail = (int) VH_TAIL.get(queuePtr, 0L);

            return head == tail;
        } finally {
            unlock();
        }
    }

    /**
     * Retrieves and removes the oldest element from the front of the queue.
     */
    public int poll() {
        lock();
        try {
            int head = (int) VH_HEAD.get(queuePtr, 0L);
            int tail = (int) VH_TAIL.get(queuePtr, 0L);

            if (head == tail) {
                return -1; // Queue empty
            }

            long arrayIndex = head & mask;
            int value = (int) elementHandle.get(this.queueSegment, 0L, 0L, arrayIndex);

            VH_HEAD.set(queuePtr, 0L, head + 1);
            return value;
        } finally {
            unlock();
        }
    }

    void put(long index, int value) {
        long arrayIndex = index & mask;
        elementHandle.set(this.queueSegment, 0L, 0L, arrayIndex, value);
    }

    int get(long index) {
        long arrayIndex = index & mask;
        return (int) elementHandle.get(this.queueSegment, 0L, 0L, arrayIndex);
    }

    void setHead(int head) {
        VH_HEAD.set(queuePtr, 0L, head);
    }

    void setTail(int tail) {
        VH_TAIL.set(queuePtr, 0L, tail);
    }

    int getHead() {
        return (int) VH_HEAD.get(queuePtr, 0L);
    }

    int getTail() {
        return (int) VH_TAIL.get(queuePtr, 0L);
    }
}