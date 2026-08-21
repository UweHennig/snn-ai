/**
 * @(#)SnnDispatcher.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import com.uwe_hennig.snn.contracts.core.TransferType;
import com.uwe_hennig.snn.services.StimulusService;

/**
 * SnnDispatcher
 *
 * @author Uwe Hennig
 */
public class SnnDispatcher {
    private static final long    QUEUE_TIMEOUT_MS = 1000;
    private static SnnDispatcher instance;

    private final IntQueue ingestQueue;
    private final IntQueue workerQueue;
    private final Thread[] workersQueueThreads;
    private final Thread   moveThread;

    private volatile boolean running  = false;
    private volatile boolean stopping = false;
    private volatile boolean shutdown = false;

    private SnnDispatcher(int ingestQueueSize, int workerQueueSize, int workerThreadSize) {
        ingestQueueSize = nextPowerTwo(ingestQueueSize);
        workerQueueSize = nextPowerTwo(workerQueueSize);

        this.ingestQueue = new IntQueue(ingestQueueSize);
        this.workerQueue = new IntQueue(workerQueueSize);
        this.workersQueueThreads = new Thread[workerThreadSize];
        moveThread = Thread.ofVirtual().unstarted(this::moveTask);
    }

    public static SnnDispatcher of(int ingestQueueSize, int workerQueueSize, int workerThreadSize) {
        if (instance == null) {
            synchronized (SnnDispatcher.class) {
                if (instance == null) {
                    instance = new SnnDispatcher(ingestQueueSize, workerQueueSize, workerThreadSize);
                }
            }
        } else {
            System.err.println("The instance already exists!");
        }
        return instance;
    }

    public static SnnDispatcher getInstance() {
        return instance;
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
        instance = null;
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
        int trgRef = StimulusService.getTargetRef(stimulusId);
        int trgType = StimulusService.getTargetType(stimulusId);
        int transferType = StimulusService.getTransferType(stimulusId);

        // TODO more cases!
        if (TransferType.DIRECT == TransferType.fromCode(transferType)) {
            // TODO check
        }
    }

    private int nextPowerTwo(int value) {
        if (value <= 4) {
            return 4;
        }
        return Integer.highestOneBit(value - 1) << 1;
    }
}
