/**
 * @(#)Trigger.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.uwe_hennig.snn.contracts.util.TriggerExecutor;

/**
 * Trigger
 *
 * @author Uwe Hennig
 */
public class Trigger {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // The latch that signals the end of the total runtime.
    private CountDownLatch latch = new CountDownLatch(1);

    private Duration        triggerInterval;
    private TriggerExecutor executor;

    private Trigger(Duration interval, TriggerExecutor executor) {
        this.triggerInterval = interval;
        this.executor = executor;
    }

    public static Trigger of(Duration interval, TriggerExecutor executor) {
        assert interval != null && executor != null : "Parameters in method 'of' must not be zero!";
        return new Trigger(interval, executor);
    }

    public void start() {
        Runnable task = () -> Thread.ofVirtual().start(() -> {
            try {
                executor.execute();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e.getLocalizedMessage());
            }
        });

        scheduler.scheduleAtFixedRate(task, triggerInterval.toMillis(), triggerInterval.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void startWithTotalRuntime(Duration totalRuntime) {
        scheduler.schedule(() -> {
            latch.countDown();
        }, totalRuntime.toMillis(), TimeUnit.MILLISECONDS);
        start();
    }

    public void awaitCompletion() {
        try {
            latch.await();
            scheduler.shutdown();
            if (!scheduler.isShutdown()) {
                if (!scheduler.awaitTermination(triggerInterval.getNano(), TimeUnit.NANOSECONDS)) {
                    scheduler.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    public void stop() {
        try {
            scheduler.shutdown();
            if (!scheduler.isShutdown()) {
                if (!scheduler.awaitTermination(triggerInterval.getNano(), TimeUnit.NANOSECONDS)) {
                    scheduler.shutdownNow();
                }
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}
