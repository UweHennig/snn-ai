/**
 * @(#)SnnExecutor.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.LockSupport;

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

    public boolean offer(int id) {
        if (!running) {
            return false;
        }

        if (queue.offer(id)) {
            return true;
        }

        // Calling thread ist too fast!

        long counter = 0;
        while (running) {
            if (queue.offer(id)) {
                return true;
            }

            counter++;
            if (counter < 100) {
                Thread.onSpinWait();
            } else if (counter < 150) {
                Thread.yield();
            } else if (counter < 200) {
                LockSupport.parkNanos(1000);
            } else {
                return false;
            }
        }
        return false;
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

    public synchronized void stop(long timeout) {
        if (!running) {
            return;
        }

        running = false;

        timeout += System.currentTimeMillis();
        while (!queue.isEmpty() && System.currentTimeMillis() < timeout) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
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

    public final class BlackHole {
        public static volatile int sink;

        public static void consume(int value) {
            sink ^= value;
        }
    }
}
