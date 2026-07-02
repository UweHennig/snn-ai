/**
 * @(#)IntQueueTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * IntQueueTest
 *
 * @author Uwe Hennig
 */
public class IntQueueTest {
    private IntQueue queue;

    @BeforeEach
    void setUp(TestInfo info) {
        queue = new IntQueue(65536);

        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterEach
    void tearDown() {
        queue.close();
    }

    @Test
    @DisplayName("Simple FIFO test")
    void testBasicFIFO() {
        queue.offer(1);
        queue.offer(2);
        queue.offer(3);

        int value = queue.poll();
        assertEquals(1, value, "FIFO rule violated");

        value = queue.poll();
        assertEquals(2, value, "FIFO rule violated");

        value = queue.poll();
        assertEquals(3, value, "FIFO rule violated");
    }

    @Test
    @DisplayName("Asynchronous access: Multiple threads write in parallel")
    void testConcurrentPutAndGet() throws InterruptedException {
        int numberOfThreads = 8;
        int operationsPerThread = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

        AtomicInteger globalIndex = new AtomicInteger(0);

        for (int i = 0; i < numberOfThreads; i++) {
            executor.submit(() -> {
                try {
                    startLatch.await();

                    for (int j = 0; j < operationsPerThread; j++) {
                        int idx = globalIndex.getAndIncrement();

                        queue.lock();
                        try {
                            queue.put(idx, idx * 2);
                        } finally {
                            queue.unlock();
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // Start all threads
        startLatch.countDown();

        boolean completed = finishLatch.await(5, TimeUnit.SECONDS);
        assertTrue(completed, "The test timed out. Possible deadlock in the locking mechanism!");

        // Validation
        int totalOperations = numberOfThreads * operationsPerThread;
        for (int i = 0; i < totalOperations; i++) {
            assertEquals(i * 2, queue.get(i), "Value at index " + i + " is incorrect.");
        }

        executor.shutdown();
    }

    @Test
    @DisplayName("Asynchronous lock test: Exclusive access guaranteed")
    void testLockExclusivity() throws InterruptedException {
        CountDownLatch thread1Locked = new CountDownLatch(1);
        CountDownLatch thread2TryLock = new CountDownLatch(1);
        AtomicInteger sharedResource = new AtomicInteger(0);

        // Thread 1 acquires the lock and holds it intentionally
        Thread thread1 = new Thread(() -> {
            queue.lock();
            try {
                sharedResource.set(42);
                thread1Locked.countDown();
                Thread.sleep(200);
                sharedResource.set(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                queue.unlock();
            }
        });

        // Thread 2 also attempts to acquire the lock immediately afterward
        Thread thread2 = new Thread(() -> {
            try {
                thread1Locked.await();

                queue.lock();
                try {
                    // If the lock works, Thread 2 should not reach this point until
                    // Thread 1 has finished and set the resource to 100.
                    thread2TryLock.countDown();
                    assertEquals(100, sharedResource.get(), "Thread 2 acquired the lock before Thread 1 was finished!");
                } finally {
                    queue.unlock();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        thread1.start();
        thread2.start();

        boolean success = thread2TryLock.await(1, TimeUnit.SECONDS);
        assertTrue(success, "Thread 2 has been permanently blocked, or the lock has not been released.");

        thread1.join();
        thread2.join();
    }

    @Test
    @DisplayName("FIFO edge cases: Full and empty queue block cleanly.")
    void testQueueBoundaries() {
        assertEquals(-1, queue.poll(), "A new queue must be empty (return -1)");

        for (int i = 0; i < 65536; i++) {
            assertTrue(queue.offer(i), "Insertion at index " + i + " failed.");
        }

        assertFalse(queue.offer(999), "The queue is full; `offer()` should have returned `false`");

        assertEquals(0, queue.poll(), "The first element (0) was not retrieved correctly");
        assertTrue(queue.offer(8888), "After a poll(), there should have been room for an offer() again");
        assertFalse(queue.offer(9999), "The queue should be full again immediately after just one space becomes available");
    }

    @Test
    @DisplayName("Asynchronous stress test: Multi-Producer and Multi-Consumer")
    void testConcurrentProducerConsumer() throws InterruptedException {
        int producersCount = 4;
        int consumersCount = 4;
        int itemsPerProducer = 5000;
        int totalItems = producersCount * itemsPerProducer;

        ExecutorService executor = Executors.newFixedThreadPool(producersCount + consumersCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(producersCount + consumersCount);

        // Tracker für die Ergebnisse
        AtomicInteger totalItemsConsumed = new AtomicInteger(0);
        LongAdder sumOfConsumedValues = new LongAdder();
        LongAdder sumOfProducedValues = new LongAdder();

        // --- PRODUCER THREADS ---
        for (int p = 0; p < producersCount; p++) {
            final int producerId = p;
            executor.submit(() -> {
                try {
                    startLatch.await();
                    for (int i = 0; i < itemsPerProducer; i++) {
                        int value = (producerId * 100000) + i;

                        while (!queue.offer(value)) {
                            Thread.onSpinWait(); // Kurz warten, falls voll
                        }
                        sumOfProducedValues.add(value);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // --- CONSUMER THREADS ---
        for (int c = 0; c < consumersCount; c++) {
            executor.submit(() -> {
                try {
                    startLatch.await();
                    // Konsumieren, bis alle erwarteten Elemente verarbeitet wurden
                    while (totalItemsConsumed.get() < totalItems) {
                        int value = queue.poll();
                        if (value != -1) {
                            sumOfConsumedValues.add(value);
                            totalItemsConsumed.incrementAndGet();
                        } else {
                            Thread.onSpinWait(); // Kurz warten, falls leer
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        boolean finishedCleanly = finishLatch.await(10, TimeUnit.SECONDS);
        assertTrue(finishedCleanly, "Timeout! Possible deadlock in the locks.");

        // --- VALIDATION ---
        assertEquals(totalItems, totalItemsConsumed.get(), "Not all elements have been consumed");
        assertEquals(sumOfProducedValues.sum(), sumOfConsumedValues.sum(),
            "The sum of the written values does not match the read values (data loss/corruption!)");
        assertEquals(-1, queue.poll(), "The queue should be completely empty at the end");

        executor.shutdown();
    }

}
