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
    public static final float EMPTY = Float.NaN;

    private static final VarHandle INT_HANDLE = ValueLayout.JAVA_INT.varHandle();

    private BufferedTransferHelper() {
    }

    /// offer
    public static boolean offer(MemorySegment segment, long queueOffset, float value) {
        int oldState;
        int count, head, tail, nextTail, nextCount, newState;
        int retryCount = 2;
        do {
            oldState = (int) INT_HANDLE.getVolatile(segment, queueOffset);
            count = (oldState >> 4) & 0x03;
            if (count == 3) {
                return false;
            }

            head = (oldState >> 2) & 0x03;
            tail = oldState & 0x03;

            // Tail rotate (e.g. with a maximum of 3 elements / size 3)
            nextTail = (tail == 2) ? 0 : tail + 1;
            nextCount = count + 1;

            // New state retains the head, updates the count and tail
            newState = (nextCount << 4) | (head << 2) | nextTail;

        } while (!INT_HANDLE.compareAndSet(segment, queueOffset, oldState, newState) && retryCount >=0);

        if (retryCount >=0) {
            segment.set(ValueLayout.JAVA_FLOAT, queueOffset + tail * 4 + 4, value);
            return true;
        }

        return false;
    }

    /// poll
    public static float poll(MemorySegment segment, long queueOffset) {
        int oldState;
        int count, head, tail, nextHead, nextCount, newState;
        int retryCount = 2;
        do {
            oldState = (int) INT_HANDLE.getVolatile(segment, queueOffset);
            count = (oldState >> 4) & 0x03;
            if (count == 0) {
                return EMPTY; // Puffer ist leer
            }

            head = (oldState >> 2) & 0x03;
            tail = oldState & 0x03;

            // Head rotieren
            nextHead = (head == 2) ? 0 : head + 1;
            nextCount = count - 1;

            newState = (nextCount << 4) | (nextHead << 2) | tail;
        } while (!INT_HANDLE.compareAndSet(segment, queueOffset, oldState, newState) && retryCount >= 0);

        if (retryCount >=0) {
            return segment.get(ValueLayout.JAVA_FLOAT, queueOffset + head * 4 + 4);
        }

        return EMPTY;
    }
}