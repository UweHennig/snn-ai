/// @(#)BufferedTransferHelper.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.util;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import com.uwe_hennig.snn.util.logging.SNNLogger;

/// BufferedTransferHelper
///
/// Status:
/// | COUNT | HEAD | TAIL | = | bx00 | bx00 | bx00 |
///
/// @author Uwe Hennig
public final class BufferedTransferHelper {
    private static final int BIT_MASK   =  0x3;
    private static final int META_CLEAR = ~0x3F;

    public static final SNNLogger log = new SNNLogger();
    private static final VarHandle INT_HANDLE = ValueLayout.JAVA_INT.varHandle();

    private BufferedTransferHelper() {
    }

    // offer
    public static boolean offer(MemorySegment segment, long queueOffset, float value) {
        int retry = 20;
        do {
            int oldStatus = (int) INT_HANDLE.getVolatile(segment, queueOffset);
            int tail  = oldStatus & BIT_MASK;
            int head  = (oldStatus >> 2) & BIT_MASK;
            int count = (oldStatus >> 4) & BIT_MASK;

            if (count >= 3) {
                return false;
            }

            int nextTail  = (tail + 1) % 3;
            int nextCount = count + 1;
            int newStatus = (oldStatus & META_CLEAR) | (nextCount << 4) | (head << 2) | nextTail;

            if (INT_HANDLE.compareAndSet(segment, queueOffset, oldStatus, newStatus)) {
                long dataOffset = queueOffset + (tail * 4L) + 4L;
                segment.set(ValueLayout.JAVA_FLOAT, dataOffset, value);
                return true;
            }
        } while (retry-- > 0);

        return false;
    }

    /// poll
    public static float poll(MemorySegment segment, long queueOffset) {
        int retry = 20;
        do {
            int oldStatus = (int) INT_HANDLE.getVolatile(segment, queueOffset);
            int tail  = oldStatus & BIT_MASK;
            int head  = (oldStatus >> 2) & BIT_MASK;
            int count = (oldStatus >> 4) & BIT_MASK;

            if (count == 0) {
                return Float.NaN; // EMPTY
            }

            long dataOffset = queueOffset + (head * 4L) + 4L;
            float value = segment.get(ValueLayout.JAVA_FLOAT, dataOffset);

            int nextHead  = (head + 1) % 3;
            int nextCount = count - 1;
            int newStatus = (oldStatus & META_CLEAR) | (nextCount << 4) | (nextHead << 2) | tail;

            if (INT_HANDLE.compareAndSet(segment, queueOffset, oldStatus, newStatus)) {
                return value;
            }
        } while (retry-- > 0);

        return Float.NaN;
    }

    // ---- Debug fields ----

    @SuppressWarnings("unused")
    private static String debugState(String phase, MemorySegment seg, long off) {
        int status = (int) INT_HANDLE.getVolatile(seg, off);
        int tail  = status & BIT_MASK;
        int head  = (status >> 2) & BIT_MASK;
        int count = (status >> 4) & BIT_MASK;

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s tail=%d head=%d count=%d ", phase, tail, head, count));
        return sb.toString();
    }

}