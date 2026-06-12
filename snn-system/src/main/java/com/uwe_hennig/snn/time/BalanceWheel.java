/**
 * @(#)BalanceWheel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.time;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * BalanceWheel
 * The clock speed was set to minimize the risk of the CPU overheating
 * @author Uwe Hennig
 */
public final class BalanceWheel {
    final AtomicBoolean running  = new AtomicBoolean(false);
    SnnClock clock;

    volatile Thread     tickThread;

    void connectTo(SnnClock clockworkPulse) {
        this.clock = clockworkPulse;
    }

    // start the timer
    void start() {
        if (!running.compareAndSet(false, true)) {
            return;
        }

        tickThread = Thread.ofVirtual().name("tick-engine").start(() -> {
            long nextTickTime = System.nanoTime();
            long tickIntervalNanos = 500; // TODO: find a good value

            while (running.get()) {
                long now = System.nanoTime();

                if (now >= nextTickTime) {
                    clock.pulse();

                    nextTickTime += tickIntervalNanos;

                    if (now > nextTickTime + tickIntervalNanos) {
                        nextTickTime = now + tickIntervalNanos;
                    }
                } else {
                    Thread.onSpinWait();
                }
            }
        });
    }

    // stop the snnClock
    void stop() {
        running.set(false);
        if (tickThread != null) {
            tickThread.interrupt();
            try {
                tickThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
