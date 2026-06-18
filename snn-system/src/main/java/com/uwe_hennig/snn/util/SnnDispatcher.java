/**
 * @(#)SnnDispatcher.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

/**
 * SnnDispatcher
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class SnnDispatcher {
    private static final long QUEUE_TIMEOUT_MS = 1000;

    private final IntQueue ingestQueue;
    private final IntQueue workerQueue;
    private final Thread[] workersQueueThreads;
    private final Thread   moveThread;

    private volatile boolean running  = false;
    private volatile boolean stopping = false;
    private volatile boolean shutdown = false;

    public SnnDispatcher(int ingestQueueSize, int workerQueueSize, int workerThreadSize) {
        ingestQueueSize = nextPowerTwo(ingestQueueSize);
        workerQueueSize = nextPowerTwo(workerQueueSize);

        this.ingestQueue = new IntQueue(ingestQueueSize);
        this.workerQueue = new IntQueue(workerQueueSize);
        this.workersQueueThreads = new Thread[workerThreadSize];
        moveThread = Thread.ofVirtual().unstarted(this::moveTask);
    }

    public void start() {
        if (running) {
            return;
        }

        for (int i = 0; i < workersQueueThreads.length; i++) {
            workersQueueThreads[i] = Thread.startVirtualThread(this::execute);
        }

        moveThread.start();

        running = true;
    }

    public void stop() {
        this.stopping = true;
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void offer(int stimulusId) {
        if (stopping || shutdown) {
            return;
        }

        long deadline = System.currentTimeMillis() + QUEUE_TIMEOUT_MS;
        while (!ingestQueue.offer(stimulusId)) {
            if (System.currentTimeMillis() > deadline || shutdown) {
                return;
            }

            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    private void moveTask() {
        while (!shutdown) {
            int stimulusId = ingestQueue.poll();
            if (stimulusId != -1) {
                // offer to workerQueue
                long deadline = System.currentTimeMillis() + QUEUE_TIMEOUT_MS;

                while (!workerQueue.offer(stimulusId)) {
                    if (System.currentTimeMillis() > deadline || shutdown) {
                        break;
                    }

                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            } else {
                if (stopping) {
                    break;
                }

                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    public void shutdown() {
        this.shutdown = true;
        this.running = false;

        for (Thread t : workersQueueThreads) {
            if (t != null) {
                t.interrupt();
            }
        }
        moveThread.interrupt();

        ingestQueue.close();
        workerQueue.close();
    }

    private void execute() {
        while (!shutdown) {
            int stimulusId = workerQueue.poll();
            if (stimulusId != -1) {
                doIt(stimulusId);
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }
    }

    protected void doIt(int stimulusId) {
        // TODO implementation
        // 1) fetch Neuronelement
        // 2) call neuronElement.stimulate(stimulusId);

        System.out.println("executing : " + stimulusId);
    }

    private int nextPowerTwo(int value) {
        if (value <= 4) {
            return 4;
        }
        return Integer.highestOneBit(value - 1) << 1;
    }
}
