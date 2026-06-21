/**
 * @(#)MultiListTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * MultiListTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class MultiListTest {
    private static final Path   TEST_FILE         = Path.of("MultiListTest.snn");
    private static final Random rand              = new Random(System.currentTimeMillis());
    private static final int    dataCapacityBytes = 200;

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterEach
    void tearDown() throws Exception {
        if (Files.exists(TEST_FILE)) {
            Files.delete(TEST_FILE);
        }
    }

    @Test
    @DisplayName("Simple writing and reading test")
    void testBasicReadWrite() {
        try (MultiList storage = new MultiList(TEST_FILE, 10, dataCapacityBytes)) {
            long offset = storage.allocate();
            int[] data = new Random().ints(0, 1000).limit(300).toArray();

            storage.put(offset, data);
            int[] result = storage.readInts(offset);

            assertArrayEquals(data, result);
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("Simple writing and reading different types")
    void testMixedTypes() {
        try (MultiList storage = new MultiList(TEST_FILE, 10, dataCapacityBytes)) {
            // --- writing
            long offset1 = storage.allocate();
            int[] data1 = {1, 2, 3, 4, 5};
            storage.put(offset1, data1);

            long offset2 = storage.allocate();
            float[] data2 = {6.1f, 6.2f, 6.3f, 6.4f, 6.5f};
            storage.put(offset2, data2);

            long offset3 = storage.allocate();
            String str = "Das ist ein Teststring";
            storage.put(offset3, str);

            // --- reading
            int[] result1 = storage.readInts(offset1);
            float [] result2 = storage.readFloat(offset2);
            String result3 = storage.readString(offset3);

            // --- check
            assertArrayEquals(data1, result1, "Invalid int array!");
            assertArrayEquals(data2, result2, "Invalid fload array!");
            assertEquals(str, result3, "Invalid  string!");

        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("NeuronElementData consistence check")
    public void dataConsistenceCheck() {
        final long maxBlocks = 10_000_000;

        // create test data
        List<int[]> values = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            final int amount = rand.nextInt(100) + 1;
            int[] slice = new int[amount];
            for (int j = 0; j < amount; j++) {
                slice[j] = i * 100 + j;
            }
            values.add(slice);
        }

        try (MultiList storage = new MultiList(TEST_FILE, maxBlocks, dataCapacityBytes)) {
            List<Long> offsets = new ArrayList<>();

            // write test data
            for (int i = 0; i < values.size(); i++) {
                long offset = storage.allocate();
                offsets.add(offset);
                storage.put(offset, values.get(i));
            }

            // read test data
            for (int j = 0; j < values.size(); j++) {
                long offset = offsets.get(j);
                System.out.print("Offset: " + offsets.get(j) + ": ");
                int[] storedData = storage.readInts(offset);
                int[] expectedData = values.get(j);

                assertEquals(expectedData.length, storedData.length);

                System.out.print("[");
                for (int i = 0; i < expectedData.length; i++) {
                    System.out.print("" + storedData[i] + ", ");
                    assertEquals(expectedData[i], storedData[i]);
                }
                System.out.println("]");

            }
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("NeuronElementData sequence check")
    void sequenceTest() {
        final long maxBlocks = 100;

        // create test data
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < 100; i++) {
            buf.append(String.format("%3d, ", i));
        }
        String longString = buf.toString();
        System.out.println("write: " + longString);

        try (MultiList storage = new MultiList(TEST_FILE, maxBlocks, 16)) {
            // write string
            long offset = storage.allocate();
            storage.put(offset, longString);

            // read string
            String data = storage.readString(offset);
            System.out.println("read:  " + data);
            assertEquals(longString, data);

        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("Multi-Threaded concurrency stress test")
    void testConcurrency() throws InterruptedException {
        int threadCount = 20;
        int writesPerThread = 50;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        Map<Long, int[]> results = new ConcurrentHashMap<>();

        try (MultiList storage = new MultiList(TEST_FILE, 100, 16)) {
            for (int i = 0; i < threadCount; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    try {
                        for (int j = 0; j < writesPerThread; j++) {
                            int[] data = IntStream.range(0, 10 + j).map(v -> v + (threadId * 1000)).toArray();

                            long offset = storage.allocate();
                            storage.put(offset, data);

                            results.put(offset, data);
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(10, TimeUnit.SECONDS);
            executor.shutdown();

            results.forEach((offset, expected) -> {
                int[] actual = storage.readInts(offset);
                assertArrayEquals(expected, actual, "NeuronElementData corruption in offset printing " + offset);
            });
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("Test persistence after restart")
    void testRestartPersistence() {
        long offset = 0L;
        int[] data = {42, 1337, 777};

        // 1. Writing and closing
        try (MultiList storage = new MultiList(TEST_FILE, 200, 16)) {
            offset = storage.allocate();
            storage.put(offset, data);
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }

        // 2. Reopen and read
        try (MultiList storage = new MultiList(TEST_FILE, 200, 16)) {
            int[] result = storage.readInts(offset);

            assertArrayEquals(data, result, "The data must still be present after the restart.");

            // 3. Check whether tail has been restored correctly (next block must be behind it)
            long nextOffset = storage.allocate();
            assertTrue(nextOffset > offset, "After a restart, tail must resume at the end of the file.");
        } catch (IOException e) {
            fail(e.getLocalizedMessage());
        }
    }
}
