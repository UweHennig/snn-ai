/**
 * @(#)SnnExecutor.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

import com.uwe_hennig.snn.time.BalanceWheel;

/**
 * SnnExecutor
 * @author Uwe Hennig
 */
public class SnnExecutor {
    private static SnnExecutor instance;

    private final IntQueue queue;
    private final TransferWorker worker;

    private final Thread[] workers;
    private final int      numberOfWorkers;

    private CountDownLatch startLatch;
    private volatile boolean running  = false;
    private volatile boolean shutdown = false;

    public SnnExecutor(long queueSize, int numberOfWorkers, TransferWorker worker) {
        this.queue = new IntQueue(queueSize);
        this.workers = new Thread[numberOfWorkers];
        this.numberOfWorkers = numberOfWorkers;
        this.worker = worker;
    }

    public static SnnExecutor of(long queueSize, int numberOfWorkers, TransferWorker worker) {
        if (instance == null) {
            synchronized (SnnExecutor.class) {
                if (instance == null) {
                    instance = new SnnExecutor(queueSize, numberOfWorkers, worker);
                }
            }
        }
        return instance;
    }

    public static SnnExecutor get() {
        return instance;
    }

    public void addTask(int id) {
        if (!running) {
            return;
        }
        queue.offer(id);
    }

    public synchronized void start() {
        if (shutdown || running) {
            return;
        }

        running = true;
        startLatch = new CountDownLatch(numberOfWorkers);

        for (int i = 0; i < numberOfWorkers; i++) {
            workers[i] = Thread.startVirtualThread(this::execute);
        }
    }

    public synchronized void stop() {
        if (!running) {
            return;
        }

        running = false;

        long timeout = System.currentTimeMillis() + 2000;
        while (!queue.isEmpty() && System.currentTimeMillis() < timeout) {
            Thread.yield();
        }

        for (Thread workerThread : workers) {
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }
    }

    public synchronized void shutdown() {
        if (shutdown) {
            return;
        }

        shutdown = true;
        running = false;

        for (Thread workerThread : workers) {
            if (workerThread != null) {
                workerThread.interrupt();
            }
        }

        if (queue != null) {
            queue.close();
        }
    }

    private void execute() {
        if (startLatch != null) {
            startLatch.countDown();
        }

        while (!Thread.currentThread().isInterrupted()) {
            if ( worker == null) {
                LockSupport.parkNanos(1_000_000L);
                continue;
            }

            int stimulusId = queue.poll();

            if (stimulusId != -1) {
                try {
                    worker.emitt(stimulusId);
                } catch (Exception e) {
                    System.err.println("Error in TransferWorker: " + e.getMessage());
                }
            } else {
                if (!running) {
                    break;
                }
                LockSupport.parkNanos(10_000L);
            }
        }
    }

}
