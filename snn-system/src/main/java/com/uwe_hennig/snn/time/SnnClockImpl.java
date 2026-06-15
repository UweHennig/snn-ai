/**
 * @(#)SnnClockImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.time;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * SnnClockImpl
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class SnnClockImpl implements BalanceWheel {
    private static SnnClockImpl INSTANCE;

    // TODO Arena Layout?
    private final AtomicLong  heartbeat = new AtomicLong(0);
    private final DoubleAdder modelTime = new DoubleAdder();
    private final AtomicLong  lastTime  = new AtomicLong(System.nanoTime());

    private final long timeWindow;
    private final long minSize;
    private final long size;

    private SnnClockImpl(long timeWindow, long minSize, long size) {
        this.timeWindow = timeWindow;
        this.minSize = minSize;
        this.size = size;
    }

    public static SnnClockImpl of(long timeWindow, long minSize, long size) {
        if (INSTANCE == null) {
            INSTANCE = new SnnClockImpl(timeWindow, minSize, size);
        }
        return INSTANCE;
    }

    public static SnnClockImpl get() {
        return INSTANCE;
    }

    public double now() {
        return modelTime.sum();
    }

    @Override
    public void beat() {
        heartbeat.incrementAndGet();
        modelTime.add(1e-7);

        long currentTime = System.nanoTime();
        long aktuelleLastTime = lastTime.get();
        long elapsed = currentTime - aktuelleLastTime;

        if (elapsed > timeWindow) {
            if (lastTime.compareAndSet(aktuelleLastTime, currentTime)) {
                long deltaBeats = heartbeat.getAndSet(0);

                if (deltaBeats > 0) {
                    double entropyTime = (size + minSize) / (double) deltaBeats;
                    modelTime.add(entropyTime);
                }
            }
        }
    }
}
