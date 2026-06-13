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

/**
 * IntQueueTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class IntQueueTest {
    private IntQueue queue;

    @BeforeEach
    void setUp() {
        queue = new IntQueue(65536);
    }

    @AfterEach
    void tearDown() {
        queue.close();
    }

    @Test
    @DisplayName("Sequential writing and reading in a single thread")
    void testBasicPutAndGet() {
        queue.put(0, 42);
        queue.put(1, 100);
        queue.put(65536, 999); // Overflow Test

        assertEquals(999, queue.get(0), "Index 65536 should overwrite index 0 because of the mask");
        assertEquals(100, queue.get(1));
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
            assertEquals(i * 2, queue.get(i), "Wert bei Index " + i + " ist inkorrekt.");
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
    @DisplayName("FIFO-Grenzfälle: Volle und leere Queue blockiert sauber")
    void testQueueBoundaries() {
        // 1. Fall: Eine frisch instanziierte Queue muss sofort -1 (leer) liefern
        assertEquals(-1, queue.poll(), "Eine neue Queue muss leer sein (-1 zurückgeben)");

        // 2. Fall: Queue bis zur maximalen Kapazität füllen
        for (int i = 0; i < 65536; i++) {
            assertTrue(queue.offer(i), "Einfügen an Index " + i + " fehlgeschlagen.");
        }

        // 3. Fall: Die Queue ist voll. Das nächste offer() MUSS false liefern
        assertFalse(queue.offer(999), "Queue ist voll, offer() hätte false liefern müssen");

        // 4. Fall: Ein Element befreien, danach muss wieder genau EIN Platz frei sein
        assertEquals(0, queue.poll(), "Das erste Element (0) wurde nicht korrekt entnommen");
        assertTrue(queue.offer(8888), "Nach einem poll() hätte wieder Platz für ein offer() sein müssen");
        assertFalse(queue.offer(9999), "Queue sollte nach nur einem Freiraum sofort wieder voll sein");
    }

    @Test
    @DisplayName("Asynchroner Belastungstest: Multi-Producer und Multi-Consumer")
    void testConcurrentProducerConsumer() throws InterruptedException {
        int producersCount = 4;
        int consumersCount = 4;
        int itemsPerProducer = 5000; // Insgesamt 20.000 Elemente
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
                    startLatch.await(); // Auf gemeinsamen Startschuss warten
                    for (int i = 0; i < itemsPerProducer; i++) {
                        int value = (producerId * 100000) + i;

                        // Da die Queue volllaufen kann, im Loop versuchen, bis Platz ist
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

        // Startschuss! Alle Threads feuern gleichzeitig los
        startLatch.countDown();

        // Maximal 10 Sekunden auf Beendigung warten
        boolean finishedCleanly = finishLatch.await(10, TimeUnit.SECONDS);
        assertTrue(finishedCleanly, "Timeout! Mögliche Verklemmung (Deadlock) in den Locks.");

        // --- VALIDIERUNG ---
        assertEquals(totalItems, totalItemsConsumed.get(), "Es wurden nicht alle Elemente konsumiert");
        assertEquals(sumOfProducedValues.sum(), sumOfConsumedValues.sum(),
            "Die Summe der geschriebenen Werte stimmt nicht mit den gelesenen Werten überein (Datenverlust/Korruption!)");
        assertEquals(-1, queue.poll(), "Die Queue sollte am Ende komplett leer sein");

        executor.shutdown();
    }

}
