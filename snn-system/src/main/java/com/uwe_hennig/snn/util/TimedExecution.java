/**
 * @(#)TimedExecution.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * TimedExecution
 *
 * @author Uwe Hennig
 */
public class TimedExecution {
    private final CountDownLatch latch    = new CountDownLatch(1);
    private final Duration       executionDuration;
    private final AtomicBoolean  runsOnce = new AtomicBoolean(true);

    public static TimedExecution of(Duration executionDuration) {
        return new TimedExecution(executionDuration);
    }

    private TimedExecution(Duration executionDuration) {
        this.executionDuration = executionDuration;
        CompletableFuture.runAsync(latch::countDown,
            CompletableFuture.delayedExecutor(executionDuration.toMillis(), TimeUnit.MILLISECONDS));
    }

    public void waitOnSignal() {
        try {
            if (runsOnce.compareAndExchange(true, false)) {
                long timeoutMillis = executionDuration.toMillis() + 100;
                latch.await(timeoutMillis, TimeUnit.MILLISECONDS);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public <T> void executeOnSignal(Runnable runnable) {
        waitOnSignal();
        runnable.run();
    }

}
