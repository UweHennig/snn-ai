/// @(#)BufferedTransferHelper.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.util;

import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

import com.uwe_hennig.snn.util.logging.SNNLogger;

//// State-Layout (one int packed):
///
///    Bit  0-1   tail        (Ringpuffer-Index 0..2)
///
///    Bit  2-3   head        (Ringpuffer-Index 0..2)
///
///    Bit  4-5   count       (Number of occupied Slots 0..3)
///
///    Bit  6-7   slot[0]     (Status Slot 0)
///
///    Bit  8-9   slot[1]     (Status Slot 1)
///
///    Bit 10-11  slot[2]     (Status Slot 2)
///
/// Slot-Status:
/// 0 = SLOT_FREE -> Offer may be reserved
/// 1 = SLOT_RESERVED -> offer is currently writing the value
/// 2 = SLOT_READY -> poll may read 3 = (unused) -> e.g. READ intermediate state, if necessary
///
/// @author Uwe Hennig
public final class BufferedTransferHelper {
    public static final SNNLogger log = new SNNLogger();
    private static final int SLOTS = 3;

    private static final int TAIL_SHIFT  = 0;
    private static final int HEAD_SHIFT  = 2;
    private static final int COUNT_SHIFT = 4;
    private static final int SLOT_SHIFT  = 6;   // Basic per sSlot +2
    private static final int FIELD_MASK  = 0b11;

    private static final int SLOT_FREE       = 0;
    private static final int SLOT_RESERVED   = 1;
    private static final int SLOT_READY      = 2;

    private static final float EMPTY = Float.NaN;

    private static final VarHandle INT_HANDLE = ValueLayout.JAVA_INT.varHandle();

    private BufferedTransferHelper() {
    }

    // offer
    public static boolean offer(MemorySegment segment, long queueOffset, float value) {
        int retry = 2;
        int oldState, newState;
        int tail;
        boolean success;

        do {
            oldState = (int) INT_HANDLE.getVolatile(segment, queueOffset);
            //log.debug(() ->  debugState("OFFER-BEGIN:", segment, queueOffset));

            int count = count(oldState);
            if (count == SLOTS) {
                return false;
            }

            tail = tail(oldState);
            if (slotStatus(oldState, tail) != SLOT_FREE) {
                return false;
            }

            newState = oldState;
            newState = withCount(newState, count + 1);
            newState = withTail (newState, nextIndex(tail));
            newState = withSlotStatus(newState, tail, SLOT_RESERVED);

            success = INT_HANDLE.compareAndSet(segment, queueOffset, oldState, newState);
        } while (!success && retry-- > 0);

        if (!success) {
            //log.debug(() ->  debugState("OFFER-MISSES:", segment, queueOffset));
            return false;
        }

        segment.set(ValueLayout.JAVA_FLOAT, queueOffset + tail * 4 + 4, value);

        // READY via CAS, not via setRelease
        int retry2 = 2;
        int s, n;
        do {
            s = (int) INT_HANDLE.getVolatile(segment, queueOffset);
            n = withSlotStatus(s, tail, SLOT_READY);
        } while (!INT_HANDLE.compareAndSet(segment, queueOffset, s, n) && retry2-- > 0);

        //log.debug(() -> debugState("OFFER-END:", segment, queueOffset));

        return true;
    }

    /// poll
    public static float poll(MemorySegment segment, long queueOffset) {
        int retry = 5;
        int oldState, newState;
        int head;
        float value;
        boolean success;

        do {
            oldState = (int) INT_HANDLE.getVolatile(segment, queueOffset);
            //log.debug(() -> debugState("POLL-BEGIN:", segment, queueOffset));

            int c = count(oldState);
            if (c == 0) {
                return EMPTY;
            }

            head = head(oldState);
            if (slotStatus(oldState, head) != SLOT_READY) {
                return EMPTY;
            }

            value = segment.get(ValueLayout.JAVA_FLOAT, queueOffset + head * 4 + 4);

            newState = oldState;
            newState = withCount(newState, c - 1);
            newState = withHead (newState, nextIndex(head));
            newState = withSlotStatus(newState, head, SLOT_FREE);
            success = INT_HANDLE.compareAndSet(segment, queueOffset, oldState, newState);
        } while (!success && retry-- > 0);

        if (!success) {
            return EMPTY;
        }

        //log.debug(() ->  debugState("POLL-END:", segment, queueOffset));

        return value;
    }

    private static int slotStatus(int state, int index) {
        return (state >> slotShift(index)) & FIELD_MASK;
    }

    private static int slotShift(int index) {
        return SLOT_SHIFT + index * 2;
    }

    // ---- Writing to state fields ----

    private static int withCount(int state, int newCount) {
        return (state & ~(FIELD_MASK << COUNT_SHIFT)) | ((newCount & FIELD_MASK) << COUNT_SHIFT);
    }

    private static int withHead(int state, int newHead) {
        return (state & ~(FIELD_MASK << HEAD_SHIFT)) | ((newHead & FIELD_MASK) << HEAD_SHIFT);
    }

    private static int withTail(int state, int newTail) {
        return (state & ~(FIELD_MASK << TAIL_SHIFT)) | ((newTail & FIELD_MASK) << TAIL_SHIFT);
    }

    private static int withSlotStatus(int state, int index, int status) {
        int shift = slotShift(index);
        return (state & ~(FIELD_MASK << shift)) | ((status & FIELD_MASK) << shift);
    }

    private static int nextIndex(int i) {
        return (i + 1) % SLOTS;
    }

    // ---- SRead state fields ----

    private static int count(int state) {
        return (state >> COUNT_SHIFT) & FIELD_MASK;
    }

    private static int head(int state) {
        return (state >> HEAD_SHIFT) & FIELD_MASK;
    }

    private static int tail(int state) {
        return (state >> TAIL_SHIFT) & FIELD_MASK;
    }

    // ---- Debug fields ----

    @SuppressWarnings("unused")
    private static String debugState(String phase, MemorySegment seg, long off) {
        int s = (int) INT_HANDLE.getVolatile(seg, off);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-12s head=%d tail=%d count=%d ", phase, head(s), tail(s), count(s)));
        sb.append("slots=[");
        for (int i = 0; i < SLOTS; i++) {
            if (i > 0) {
                sb.append(' ');
            }
            sb.append(slotChar(slotStatus(s, i)));
        }
        sb.append("] raw=0b").append(Integer.toBinaryString(s));
        return sb.toString();
    }

    private static String slotChar(int id) {
        return switch(id) {
            case 0 ->  "."; // FREE
            case 1 ->  "!"; // RESERVED
            case 2 ->  "R"; // READY
            default -> "?"; // UNDEFINED
        };
    }

}