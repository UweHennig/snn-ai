/// @(#)BufferedTransferHelper.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.util;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/// BufferedTransferHelper
/// ````
/// ````
/// @author Uwe Hennig
public final class BufferedTransferHelper {
    private static final int LOCKED_BIT  = 0b001;
    private static final int COUNT_SHIFT = 1;
    private static final int COUNT_MASK  = 0b11 << COUNT_SHIFT;
    private static final int COUNT_MAX   = 3;

    private static final int LOCK_OFFSET = 12;

    private static final int SLOT_A = 0;
    private static final int SLOT_B = 4;
    private static final int SLOT_C = 8;

    public static final float EMPTY = Float.MIN_VALUE;

    private static final VarHandle INT_HANDLE   = ValueLayout.JAVA_INT.varHandle();
    private static final VarHandle FLOAT_HANDLE = ValueLayout.JAVA_FLOAT.varHandle();

    private BufferedTransferHelper() {
    }

    /// offer
    public static boolean offer(MemorySegment segment, long queueOffset, float value) {
        long lockOffset = queueOffset + LOCK_OFFSET;
        if (!tryLock(segment, lockOffset)) {
            return false;
        }

        try {
            int state = readState(segment, queueOffset + LOCK_OFFSET);
            int n = getCount(state);

            if (n >= COUNT_MAX) {
                return false;
            }

            long slot = switch (n) {
                case 0 -> SLOT_A;
                case 1 -> SLOT_B;
                case 2 -> SLOT_C;
                default -> -1;
            };

            if (slot == -1) {
                return false;
            }

            FLOAT_HANDLE.set(segment, queueOffset + slot, value);

            int newState = withCount(state, n + 1) | LOCKED_BIT;
            INT_HANDLE.setRelease(segment, lockOffset, newState);
            return true;
        } finally {
            unlock(segment, lockOffset);
        }
    }

    /// poll
    public static float poll(MemorySegment segment, long queueOffset) {
        long lockOffset = queueOffset + LOCK_OFFSET;
        if (!tryLock(segment, lockOffset)) {
            return EMPTY;
        }
        try {
            int state = readState(segment, lockOffset);
            int n = getCount(state);

            if (n == 0) {
                return EMPTY;
            }

            float head = (float) FLOAT_HANDLE.get(segment, queueOffset + SLOT_A);

            float b = (float) FLOAT_HANDLE.get(segment, queueOffset + SLOT_B);
            float c = (float) FLOAT_HANDLE.get(segment, queueOffset + SLOT_C);

            FLOAT_HANDLE.set(segment, queueOffset + SLOT_A, b);
            FLOAT_HANDLE.set(segment, queueOffset + SLOT_B, c);

            int newState = withCount(state, n - 1) | LOCKED_BIT;
            INT_HANDLE.setRelease(segment, lockOffset, newState);

            return head;
        } finally {
            unlock(segment, lockOffset);
        }
    }

    // --- lock ---

    /// Simple try-lock
    static boolean tryLock(MemorySegment segment, long lockOffset) {
        int cur = (int) INT_HANDLE.getVolatile(segment, lockOffset);
        if ((cur & LOCKED_BIT) != 0) {
            return false;
        }
        int next = cur | LOCKED_BIT;
        if (INT_HANDLE.compareAndSet(segment, lockOffset, cur, next)) {
            return true;
        }
        return false;
    }

    /// The method may only be called if you yourself possess the lock!
    static void unlock(MemorySegment segment, long lockOffset) {
        int cur = (int) INT_HANDLE.getVolatile(segment, lockOffset);
        int next = cur & ~LOCKED_BIT;
        if (INT_HANDLE.compareAndSet(segment, lockOffset, cur, next)) {
            return;
        }
    }
    // --- convenient methods ---

    /// Returns the state without the lock bit
    static int readState(MemorySegment segment, long lockOffset) {
        return (int) INT_HANDLE.get(segment, lockOffset) & ~LOCKED_BIT;
    }

    /// Extracts the count value from the state
    static int getCount(int state) {
        return (state & COUNT_MASK) >>> COUNT_SHIFT;
    }

    /// Sets the count and leaves lock unchanged
    static int withCount(int state, int n) {
        return (state & ~COUNT_MASK) | (n << COUNT_SHIFT);
    }

    /// Returns the status unchanged
    static int getState(MemorySegment segment, long queueOffset) {
        long lockOffset = queueOffset + LOCK_OFFSET;
        return readState(segment, lockOffset);
    }

}
