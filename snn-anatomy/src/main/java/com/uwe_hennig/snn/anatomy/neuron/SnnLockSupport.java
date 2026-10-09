/// @(#)LockSupport.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.concurrent.locks.LockSupport;

/// LockSupport
///
/// @author Uwe Hennig
public final class SnnLockSupport {
    private static final int WRITER_ACTIVE  = 0x80000000;
    private static final int WRITER_WAITING = 0x40000000;
    private static final int READER_MASK    = 0x3FFFFFFF;

    private static final VarHandle INT_HANDLE = ValueLayout.JAVA_INT.varHandle();

    private final MemorySegment segment;

    public SnnLockSupport(MemorySegment segment) {
        this.segment = segment;
    }

    // ------------------------------------------------------------------
    // Writer
    // ------------------------------------------------------------------

    void writeLock(int offset) {
        // Phase 1: WRITER_WAITING set – prevents new readers (writer-preferring).
        int spins = 0;
        while (true) {
            int current = (int) INT_HANDLE.getVolatile(segment, offset);

            if ((current & WRITER_WAITING) != 0) {
                break; // Already set – OK
            }
            if (INT_HANDLE.compareAndSet(segment, offset,
                    current, current | WRITER_WAITING)) {
                break;
            }
            sleep(spins++);
        }

        // Phase 2: Wait until there are no more active readers, then set WRITER_ACTIVE.
        spins = 0;
        while (true) {
            int current = (int) INT_HANDLE.getVolatile(segment, offset);

            // The reader counter must be 0; WRITER_ACTIVE has not yet been set.
            if ((current & READER_MASK) == 0
                    && (current & WRITER_ACTIVE) == 0) {
                int next = current | WRITER_ACTIVE;
                if (INT_HANDLE.compareAndSet(segment, offset, current, next)) {
                    return;
                }
            }
            sleep(spins++);
        }
    }

    void writeUnlock(int offset) {
        int current = (int) INT_HANDLE.getVolatile(segment, offset);
        if ((current & WRITER_ACTIVE) == 0) {
            throw new IllegalMonitorStateException("writeUnlock without writeLock");
        }
        // Release: Make all write operations visible; set Lock to 0.
        INT_HANDLE.setRelease(segment, offset, 0);
    }

    // ------------------------------------------------------------------
    // Reader
    // ------------------------------------------------------------------

    void readLock(int offset) {
        int spins = 0;
        while (true) {
            int current = (int) INT_HANDLE.getVolatile(segment, offset);

            // Writer active or waiting → Block reader (writer-preferring).
            if ((current & (WRITER_ACTIVE | WRITER_WAITING)) != 0) {
                sleep(spins++);
                continue;
            }

            // Increase the number of readers.
            if ((current & READER_MASK) == READER_MASK) {
                throw new IllegalStateException("Reader counter overflow");
            }
            if (INT_HANDLE.compareAndSet(segment, offset, current, current + 1)) {
                return;
            }
        }
    }

    void readUnlock(int offset) {
        int prev = (int) INT_HANDLE.getAndAddRelease(segment, offset, -1);
        if ((prev & READER_MASK) == 0) {
            INT_HANDLE.getAndAdd(segment, offset, 1);
            throw new IllegalMonitorStateException("readUnlock without readLock");
        }
    }

    // ------------------------------------------------------------------
    // Sleep: a bit of spinning, then micro-breaks
    // ------------------------------------------------------------------

    private static void sleep(int spins) {
        if (spins < 8) {
            Thread.onSpinWait();
        } else if (spins < 32) {
            Thread.yield();
        } else {
            LockSupport.parkNanos(100);
        }
    }}
