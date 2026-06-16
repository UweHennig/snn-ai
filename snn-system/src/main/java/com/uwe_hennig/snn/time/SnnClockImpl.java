/**
 * @(#)SnnClockImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.time;

import java.lang.foreign.Arena;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.StructLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;

/**
 * SnnClockImpl
 * @author Uwe Hennig
 */
public class SnnClockImpl implements BalanceWheel {
    // Singleton via "Initialization-on-demand holder" (Thread-safe & Lazy)
    private static class Holder {
        static SnnClockImpl INSTANCE;
    }

    // @formatter:off
    private static final StructLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("lock"),
        MemoryLayout.paddingLayout(4),
        ValueLayout.JAVA_LONG.withName("heartbeat"),
        ValueLayout.JAVA_DOUBLE.withName("modelTime"),
        ValueLayout.JAVA_LONG.withName("lastTime")
    ).withByteAlignment(8);

    private static final VarHandle VH_LOCK = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lock"));
    private static final VarHandle VH_HEARTBEAT = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("heartbeat"));
    private static final VarHandle VH_MODEL_TIME = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("modelTime"));
    private static final VarHandle VH_LAST_TIME = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lastTime"));
    // @formatter:on

    private final Arena arena;
    private final MemorySegment segment;
    private final long timeWindow;
    private final long minSize;
    private final long size;

    private SnnClockImpl(long timeWindow, long minSize, long size) {
        // Shared Arena ist korrekt für Multi-Threading
        this.arena = Arena.ofShared();
        this.segment = arena.allocate(LAYOUT);

        // Initialization
        VH_LAST_TIME.set(segment, 0L, System.nanoTime());

        this.timeWindow = timeWindow;
        this.minSize = minSize;
        this.size = size;
    }

    public static SnnClockImpl of(long timeWindow, long minSize, long size) {
        // Double-Checked Locking für Singleton
        if (Holder.INSTANCE == null) {
            synchronized (SnnClockImpl.class) {
                if (Holder.INSTANCE == null) {
                    Holder.INSTANCE = new SnnClockImpl(timeWindow, minSize, size);
                }
            }
        }
        return Holder.INSTANCE;
    }

    public static SnnClockImpl get() {
        return Holder.INSTANCE;
    }

    /**
     * Retrieves the current model time with high performance without a lock.
     * getVolatile ensures that we see the most up-to-date value from RAM/cache.
     */
    public double now() {
        return (double) VH_MODEL_TIME.getVolatile(segment, 0L);
    }

    // ----- lock/unlock -----

    private void lock() {
        while ((int) VH_LOCK.compareAndExchange(segment, 0L, 0, 1) != 0) {
            Thread.onSpinWait();
        }
    }

    private void unlock() {
        VH_LOCK.setRelease(segment, 0L, 0);
    }

    // ----- Atomic helper methods -----

    public void incrementHeartbeat() {
        VH_HEARTBEAT.getAndAdd(segment, 0L, 1L);
    }

    public long getHeartbeat() {
        return (long) VH_HEARTBEAT.getVolatile(segment, 0L);
    }

    @Override
    public void beat() {
        lock();
        try {
            double currentModelTime = (double) VH_MODEL_TIME.get(segment, 0L);
            currentModelTime += 1e-7;

            long currentTime = System.nanoTime();
            long lastTime = (long) VH_LAST_TIME.get(segment, 0L);
            long elapsed = currentTime - lastTime;

            if (elapsed > timeWindow) {
                VH_LAST_TIME.set(segment, 0L, currentTime);

                long deltaBeats = (long) VH_HEARTBEAT.getAndSet(segment, 0L, 0L);
                if (deltaBeats > 0) {
                    double entropyTime = (size + minSize) / (double) deltaBeats;
                    currentModelTime += entropyTime;
                }
            } else {
                VH_HEARTBEAT.getAndAdd(segment, 0L, 1L);
            }

            VH_MODEL_TIME.setVolatile(segment, 0L, currentModelTime);

        } finally {
            unlock();
        }
    }

    public void close() {
        arena.close();
    }
}