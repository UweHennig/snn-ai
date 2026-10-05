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
    private static final int WRITER_WAITING  = 0x40000000; // Bit 30
    private static final int WRITER_ACTIVE   = 0xFFFFFFFF; // -1
    private static final VarHandle IntHandle = ValueLayout.JAVA_INT.varHandle();

    private final MemorySegment segment;

    public SnnLockSupport(MemorySegment segment) {
        this.segment = segment;
    }

    void writeLock(int offset) {
        int spins = 0;
        while (true) {
            int current = (int) IntHandle.getVolatile(segment, offset);

            if ((current & WRITER_WAITING) == 0) {
                if (IntHandle.compareAndSet(segment, offset, current, current | WRITER_WAITING)) {
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
            int current = (int) IntHandle.getVolatile(segment, offset);

            // no reader active and write flag is set or initial state.
            if (current == WRITER_WAITING || current == 0) {
                if (IntHandle.compareAndSet(segment, offset, current, WRITER_ACTIVE)) {
                    return;
                }
            }

            backoff(spins++);
        }
    }

    boolean readLock(int offset) {
        int spins = 0;
        while (true) {
            int current = (int) IntHandle.getVolatile(segment, offset);

            // give priority to the writers
            if (current < 0 || (current & WRITER_WAITING) != 0) {
                backoff(spins++);
                continue;
            }

            // increment reader counter
            if (IntHandle.compareAndSet(segment, offset, current, current + 1)) {
                return true;
            }
        }
    }

    void writeUnlock(int offset) {
        IntHandle.setRelease(segment, offset, 0);
    }

    void readUnlock(int offset) {
       // decrement reader counter
        IntHandle.getAndAdd(segment, offset, -1);
    }

    private void backoff(int spins) {
        if (spins < 16) {
            Thread.onSpinWait();
        } else {
            LockSupport.parkNanos(1);
        }
    }
}
