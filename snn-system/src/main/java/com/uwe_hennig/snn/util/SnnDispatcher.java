/**
 * @(#)SnnDispatcher.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

/**
 * SnnDispatcher
 *
 * @author Uwe Hennig
 */
public final class SnnDispatcher {
    private static final long    QUEUE_TIMEOUT_MS = 1000;
    private static SnnDispatcher instance;

    private final EventQueue ingestQueue;
    private final EventQueue workerQueue;

    private final Thread[]   workersQueueThreads;
    private final Thread     moveThread;

    private volatile boolean running  = false;
    private volatile boolean stopping = false;
    private volatile boolean shutdown = false;

    private SnnDispatcher(EventQueue ingestQueue, EventQueue workerQueue, int workerThreadSize) {
        this.ingestQueue = ingestQueue;
        this.workerQueue = workerQueue;

        this.workersQueueThreads = new Thread[workerThreadSize];
        moveThread = Thread.ofVirtual().unstarted(this::moveTask);
    }

    public static SnnDispatcher of(EventQueue ingestQueue, EventQueue workerQueue, int workerThreadSize) {
        if (instance == null) {
            synchronized (SnnDispatcher.class) {
                if (instance == null) {
                    instance = new SnnDispatcher(ingestQueue, workerQueue, workerThreadSize);
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

    public void offer(int tapeId, int length) {
        if (stopping || shutdown) {
            return;
        }

        long deadline = System.currentTimeMillis() + QUEUE_TIMEOUT_MS;
        while (!ingestQueue.enqueue(tapeId, length)) {
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
            int [] values = ingestQueue.dequeue();
            if (values != null && values.length == 2) {
                // offer to workerQueue
                long deadline = System.currentTimeMillis() + QUEUE_TIMEOUT_MS;

                while (!workerQueue.enqueue(values[0], values[1])) {
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
            int [] values = workerQueue.dequeue();
            if (values != null && values.length == 2) {
                doIt(values);
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

    protected void doIt(int [] values) {
        int tapeId = values[0];
        int length = values[1];
        // TODO
    }
}
