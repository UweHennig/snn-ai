/**
 * @(#)StimulusModel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;
import static java.lang.foreign.ValueLayout.JAVA_LONG;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.LockSupport;

/**
 * StimulusModel
 * TODO expiry float or long
 * @author Uwe Hennig
 */
public class StimulusModel {
    final int   capacity;
    final Arena arena;

    SequenceLayout sequenceLayout;
    MemorySegment  segment;

    // @formatter:off
    public static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        JAVA_INT.withName("lock"),
        JAVA_INT.withName("eventType"),
        JAVA_INT.withName("edgeRef"),
        JAVA_FLOAT.withName("value"),
        JAVA_LONG.withName("expiry")
    ).withByteAlignment(8);

    static final VarHandle VH_LOCK       = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("lock"));
    static final VarHandle VH_EVENT_TYPE = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("eventType"));
    static final VarHandle VH_EDGE_REF   = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("edgeRef"));
    static final VarHandle VH_EXPIRY     = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("expiry"));
    static final VarHandle VH_VALUE      = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("value"));
    // @formatter:on

    public StimulusModel(int capacity) {
        this.capacity = capacity;
        this.arena = Arena.ofShared();

        this.sequenceLayout = MemoryLayout.sequenceLayout(capacity, LAYOUT);
        this.segment = arena.allocate(sequenceLayout);
    }

    public void close() {
        arena.close();
    }

    public int getCapacity() {
        return capacity;
    }

    // ----- lock/unlock -----

    private static final int WRITER_WAITING = 0x40000000; // Bit 30
    private static final int WRITER_ACTIVE  = 0xFFFFFFFF; // -1

    void writeLock(int index) {
        int spins = 0;
        // set the WRITER_WAITING flag to indicate a write request
        while (true) {
            int current = (int) VH_LOCK.getVolatile(segment, 0L, index);
            if ((current & WRITER_WAITING) == 0) {
                if (VH_LOCK.compareAndSet(segment, 0L, index, current, current | WRITER_WAITING)) {
                    break;
                }
            } else {
                break;
            }
            backoff(spins++);
        }

        // set the lock
        spins = 0;
        while (true) {
            int current = (int) VH_LOCK.getVolatile(segment, 0L, index);

            // no reader aktive and write flag is set or initial state.
            if (current == WRITER_WAITING || current == 0) {
                if (VH_LOCK.compareAndSet(segment, 0L, index, current, WRITER_ACTIVE)) {
                    return;
                }
            }

            backoff(spins++);
        }
    }

    void writeUnlock(int index) {
        VH_LOCK.setRelease(segment, 0L, index, 0);
    }

    boolean readLock(int index) {
        int spins = 0;
        while (true) {
            int current = (int) VH_LOCK.getVolatile(segment, 0L, index);

            // give priority to the writers
            if (current < 0 || (current & WRITER_WAITING) != 0) {
                backoff(spins++);
                continue;
            }

            // increment reader counter
            if (VH_LOCK.compareAndSet(segment, 0L, index, current, current + 1)) {
                return true;
            }
        }
    }

    void readUnlock(int index) {
       // decrement reader counter
       VH_LOCK.getAndAdd(segment, 0L, index, -1);
    }

    boolean tryWriteLock(long index) {
        return (int) VH_LOCK.compareAndExchange(segment, 0L, index, 0, -1) == 0;
    }

    boolean isWriteLocked(long index) {
        return (int) VH_LOCK.get(segment, 0L, index) == -1;
    }

    void backoff(int spins) {
        if (spins < 64) {
            Thread.onSpinWait();
        } else {
            LockSupport.parkNanos(1);
        }
    }

    // ----- getter/setter -----

    int getEventType(int index) {
        return (int) VH_EVENT_TYPE.get(segment, 0L, index);
    }

    void setEventType(int index, int value) {
        VH_EVENT_TYPE.set(segment, 0L, index, value);
    }

    int getEdgeRef(int index) {
        return (int) VH_EDGE_REF.get(segment, 0L, index);
    }

    void setEdgeRef(int index, int value) {
        VH_EDGE_REF.set(segment, 0L, index, value);
    }

    float getValue(int index) {
        return (float) VH_VALUE.get(segment, 0L, index);
    }

    void setValue(int index, float value) {
        VH_VALUE.set(segment, 0L, index, value);
    }

    long getExpiry(int index) {
        return (long) VH_EXPIRY.get(segment, 0L, index);
    }

    void setExpiry(int index, long value) {
        VH_EXPIRY.set(segment, 0L, index, value);
    }
}
