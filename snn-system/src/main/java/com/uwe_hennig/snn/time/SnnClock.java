/**
 * @(#)SnnClock.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.time;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SnnClock
 * In this implementation, model time is determined by the system’s activities.
 * Unlike conventional model-time approaches, processing is not governed by a predefined temporal rhythm.
 * @author Uwe Hennig
 */
public class SnnClock {
    private ExecutorService     executor = Executors.newVirtualThreadPerTaskExecutor();
    public static volatile long counter  = 1L;
    private final BalanceWheel  balanceWheel;

    private final ReentrantLock                 lock  = new ReentrantLock();
    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

    private static SnnClock INSTANCE;

    private SnnClock() {
        this.balanceWheel = new BalanceWheel();
        this.balanceWheel.connectTo(this);
    }

    public static SnnClock instance() {
        if (INSTANCE == null) {
            INSTANCE = new SnnClock();
        }
        return INSTANCE;
    }

    public void start() {
        balanceWheel.start();
    }

    public void stop() {
        balanceWheel.stop();
        executor.shutdown();
    }

    public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException{
        if (!executor.isShutdown()) {
            executor.shutdown();
        }
        if (!executor.awaitTermination(timeout, unit)) {
            System.err.println("Warning: Not all tasks were completed on time.");
        }
    }

    void pulse() {
        if (lock.tryLock()) {
            Runnable task;
            try {
                for (int i = 0; i < 10; i++) {
                    task = queue.poll();
                    if (task == null) {
                        break;
                    }
                    executor.submit(task);
                    counter++;
                }
            } finally {
                lock.unlock();
            }

        }
    }

    public long now() {
        return counter;
    }

    public void submit(Runnable task) {
        queue.offer(task);
    }
}
