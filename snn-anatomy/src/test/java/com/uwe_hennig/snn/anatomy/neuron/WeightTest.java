/**
 * @(#)WeightTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * WeightTest
 *
 * @author Uwe Hennig
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WeightTest {
    private static final int        NUM_THREADS = 10;
    private static final int        RW_CAPACITY = 1_000_000;
    private static final AtomicLong counter     = new AtomicLong();

    @Test
    @DisplayName("Simple MemoryModel Test")
    public void testMemoryModel() {
        WeightModel model = null;
        try {
            model = new WeightModel(1);
            checkModel(model, 1);

            model.writeLock(0);
            model.setWeight(0, 1f);
            assertEquals(1f, model.getWeight(0), "invalid weight in model");

            model.setPreSynapticTime(0, 2f);
            assertEquals(2f, model.getPreSynapticTime(0), "invalid preSynapticTime in model");

            model.setPostSynapticTime(0, 3f);
            assertEquals(3f, model.getPostSynapticTime(0), "invalid postSynapticTime in model");

            model.setHebbTimeRange(0, 4f);
            assertEquals(4f, model.getHebbTimeRange(0), "invalid hebbTimeRange in model");

            model.setHebbScale(0, 5f);
            assertEquals(5f, model.getHebbScale(0), "invalid hebbScale in model");

            model.setWeightScale(0, 6f);
            assertEquals(6f, model.getWeightScale(0), "invalid weightScale in model");

            model.setTimeLimit(0, 7f);
            assertEquals(7f, model.getTimeLimit(0), "invalid timeLimit in model");
            model.writeUnlock(0);
        } finally {
            if (model != null) {
                model.close();
            }
        }
    }

    @Test
    @DisplayName("Lock Test on WeightModel")
    public void testLock() {
        final int capacity = 1;
        final WeightModel model = new WeightModel(capacity);
        checkModel(model, capacity);

        AtomicLong lockCount = new AtomicLong(0);
        AtomicLong lockTime = new AtomicLong(0);

        CountDownLatch startSignal = new CountDownLatch(1);

        try {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    try {
                        startSignal.await();
                        ThreadLocalRandom rand = ThreadLocalRandom.current();

                        int v = 0; // hotspot lock
                        boolean w = rand.nextBoolean();

                        if (w) {
                            long start = System.nanoTime();
                            model.writeLock(v); // measure lock acquisition
                            long end = System.nanoTime();

                            lockTime.addAndGet(end - start);
                            lockCount.incrementAndGet();

                            try {
                                model.setWeight(v, rand.nextFloat());
                            } finally {
                                model.writeUnlock(v);
                            }
                        } else {
                            float r = model.getWeight(v);
                            Blackhole.consume(r);
                        }
                    } catch (Exception e) {
                        fail("Exception in testLock: " + e.getLocalizedMessage());
                    }
                });
            }

            startSignal.countDown(); // simultaneous start

            long totalStart = System.nanoTime();
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            long totalEnd = System.nanoTime();

            System.out.printf("%nLock count        : %,d", lockCount.get());
            System.out.printf("%nLock waiting      : %,5.2f µs", lockTime.get() / lockCount.get() / 1_000f);
            System.out.printf("%nThread total time : %,5.2f msec%n", (totalEnd - totalStart) / 1_000_000f);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        } finally {
            model.close();
        }
    }

    @Test
    @DisplayName("Asynchronous Test on WeightModel")
    public void testAsyncModel() {
        final int capacity = 10;
        final WeightModel model = new WeightModel(capacity);
        checkModel(model, capacity);

        try {
            ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

            for (int i = 0; i < NUM_THREADS; i++) {
                executor.submit(() -> {
                    final ThreadLocalRandom rand = ThreadLocalRandom.current();
                    int v = rand.nextInt(capacity);

                    float currentWeight = rand.nextFloat();
                    float timestamp = rand.nextFloat();
                    try {
                        model.writeLock(v);
                        model.setWeight(v, currentWeight);
                        model.setPreSynapticTime(v, timestamp);
                    } finally {
                        model.writeUnlock(v);
                    }
                });
            }
            Thread.sleep(1000);
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        } finally {
            model.close();
        }
    }

    @Test
    @DisplayName("Performance Test on WeightModel")
    public void testPerformance() {
        final int capacity = 4000;
        final int TEST_DURATION_SECONDS = 10;
        final int NUM_THREADS = 2000;
        System.out.printf("%nProcessors     : %5d", Runtime.getRuntime().availableProcessors());
        System.out.printf("%nVirtual threads: %5d", NUM_THREADS);

        final WeightModel model = new WeightModel(capacity);
        checkModel(model, capacity);

        // Flag to stop the threads
        AtomicBoolean running = new AtomicBoolean(true);
        AtomicLong operations = new AtomicLong();
        Thread.onSpinWait();

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

        for (int i = 0; i < NUM_THREADS; i++) {
            executor.submit(() -> {
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                while (running.get()) {
                    int v = rand.nextInt(capacity);

                    float currentWeight = rand.nextFloat();
                    float timestamp = rand.nextFloat();

                    try {
                        model.writeLock(v);
                        model.setWeight(v, currentWeight);
                        model.setPreSynapticTime(v, timestamp);
                    } finally {
                        model.writeUnlock(v);
                    }

                    // Prevent JIT from optimizing the read away
                    float w = model.getWeight(v);
                    float t = model.getPreSynapticTime(v);

                    Blackhole.consume(w + t);

                    operations.incrementAndGet();
                }
            });
        }

        try {
            long totalOps = 0;
            for (int sec = 1; sec <= TEST_DURATION_SECONDS; sec++) {
                Thread.sleep(1000);
                long ops = operations.getAndSet(0);
                totalOps += ops;
                System.out.printf("\tSecond %,d: %,d calculations%n", sec, ops);
            }

            running.set(false);
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);

            double avgOpsPerSec = (double) totalOps / TEST_DURATION_SECONDS;
            System.out.println();
            System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
            System.out.printf("Latency    : %,13.2f ns/op%n", 1_000_000_000f / avgOpsPerSec);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            model.close();
        }
    }

    @Test
    @Order(1)
    @DisplayName("Write to File Test")
    public void testWrite() {
        final WeightModel model = new WeightModel(RW_CAPACITY);
        float values = 0f;
        System.out.printf("Writing %,d records!%n", RW_CAPACITY);

        try {
            for (int i = 0; i < RW_CAPACITY; i++) {
                try {
                    model.writeLock(i);
                    model.setWeight(i, values);
                    model.setHebbTimeRange(i, values + 1f);
                    values += 2f;
                } finally {
                    model.writeUnlock(i);
                }
            }

            Path path = getArenaPath("weight.data");

            long startTime = System.nanoTime();
            WeightPersistence.save(model, path);
            long endTime = System.nanoTime();

            System.out.printf("Written %,d bytes %n", model.segment.byteSize());
            System.out.printf("Written in %,3.2f milli sec%n%n", (endTime - startTime) / 1_000_000f);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        } finally {
            model.close();
        }
    }

    @Test
    @Order(2)
    @DisplayName("Read from File Test")
    public void testRead() {
        WeightModel model = null;
        try {
            System.out.printf("Read %,d records!%n", RW_CAPACITY);
            Path path = getArenaPath("weight.data");
            assertTrue(path.toFile().exists(), "file not created " + path);

            long startTime = System.nanoTime();
            model = WeightPersistence.load(path);
            long endTime = System.nanoTime();

            assertNotNull(model, "Model not created!");

            float values = 0f;
            for (int i = 0; i < RW_CAPACITY; i++) {
                assertEquals(values, model.getWeight(i));
                assertEquals(values + 1f, model.getHebbTimeRange(i));
                values += 2;
            }

            System.out.printf("Read %,d bytes %n", model.segment.byteSize());
            System.out.printf("Read in %,3.1f milli sec%n%n", (endTime - startTime) / 1_000_000f);

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    private void checkModel(WeightModel model, int capacity) {
        System.out.printf("%nModel information");
        System.out.printf("%nCapacity       : %6d", model.capacity);
        System.out.printf("%nLayout size    : %6d bytes", WeightModel.LAYOUT.byteSize());
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());
        System.out.printf("%nStructure      : %s%n%n", WeightModel.LAYOUT);

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertNotNull(model.sequenceLayout, "SequenceLayout is null!");

        assertEquals(capacity, model.capacity, "Invalid capacity!");
        assertEquals(WeightModel.LAYOUT.byteSize() * model.capacity, model.segment.byteSize(), "Invalid segment size!");
    }

    private static Path getArenaPath(String fileName) {
        String envPath = System.getenv("SNN_STORAGE_PATH");
        if (envPath != null) {
            return Path.of(envPath, fileName);
        }

        Path localPath = Path.of(System.getProperty("user.dir"), "data", "arena", fileName);

        try {
            Files.createDirectories(localPath.getParent());
        } catch (IOException ignored) {
        }

        return localPath;
    }

    public final class Blackhole {
        private static volatile Object SINK;

        public static void consume(Object v) {
            SINK = v;
            long c = counter.incrementAndGet();
            if (c % 10_000_000L == 0L) {
                System.out.print(".");
            }
        }

        public static Object getSink() {
            return SINK;
        }
    }

    @AfterEach
    public void afterEach() {
        counter.set(0);
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterAll
    public static void clean() {
        Path path = getArenaPath("weight.data");
        if (path.toFile().exists()) {
            path.toFile().delete();
        }
    }

}
