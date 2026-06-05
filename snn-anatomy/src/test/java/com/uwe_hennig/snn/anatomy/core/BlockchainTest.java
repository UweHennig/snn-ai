/**
 * @(#)BlockchainTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * BlockchainTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class BlockchainTest {
    private final static String FILENAME = "BlockchainTest.idx";

    private final long MAX_BLOCKS          = 100;
    private final int  MAX_ROW_BYTE_LENGTH = 128;
    private final int  NUM_THREADS         = 5;

    @Test
    @DisplayName("Simple Blockchain Test")
    public void testSimple() {
        Blockchain blockchain = new Blockchain(MAX_BLOCKS, MAX_ROW_BYTE_LENGTH);

        String txt = "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam";
        long startString = blockchain.allocate();
        blockchain.put(startString, txt);
        assertEquals(txt, blockchain.getString(startString));

        long[] twoRowData = { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L };
        long startRowData = blockchain.allocate();
        blockchain.put(startRowData, twoRowData);
        assertArrayEquals(twoRowData, blockchain.getLongs(startRowData));

        long[] longData = { 1L, 2L, 3L };
        long startLong = blockchain.allocate();
        blockchain.put(startLong, longData);
        assertArrayEquals(longData, blockchain.getLongs(startLong));

        int[] intData = { 4, 5, 6 };
        long startInt = blockchain.allocate();
        blockchain.put(startInt, intData);
        assertArrayEquals(intData, blockchain.getInts(startInt));

        float[] floatData = { 7f, 8f, 9f };
        long startFloat = blockchain.allocate();
        blockchain.put(startFloat, floatData);
        assertArrayEquals(floatData, blockchain.getFloat(startFloat));

        // Back
        assertEquals(txt, blockchain.getString(startString));
        assertArrayEquals(twoRowData, blockchain.getLongs(startRowData));
        assertArrayEquals(longData, blockchain.getLongs(startLong));
        assertArrayEquals(intData, blockchain.getInts(startInt));
        assertArrayEquals(floatData, blockchain.getFloat(startFloat));

        blockchain.close();
    }

    @Test
    @DisplayName("Simple Blockchain Delete Test")
    public void testDelete() {
        Blockchain blockchain = new Blockchain(MAX_BLOCKS, MAX_ROW_BYTE_LENGTH);

        long[] twoRowData = { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L };
        long startRowData = blockchain.allocate();
        blockchain.put(startRowData, twoRowData);
        assertArrayEquals(twoRowData, blockchain.getLongs(startRowData));

        blockchain.delete(startRowData);
        assertNull(blockchain.getLongs(startRowData));
        blockchain.close();
    }

    @Test
    @DisplayName("Blockchain multiple Put Test")
    public void testPut() {
        // TODO Check whether the memory overflow is being utilized or whether a new allocation is being made
        Blockchain blockchain = new Blockchain(MAX_BLOCKS, MAX_ROW_BYTE_LENGTH);

        long[] listA = { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L };
        long startRowData = blockchain.allocate();
        blockchain.put(startRowData, listA);
        assertArrayEquals(listA, blockchain.getLongs(startRowData));

        long[] listB = { 19L, 20L };
        blockchain.put(startRowData, listB);
        assertArrayEquals(listB, blockchain.getLongs(startRowData));

        long[] listC = { 1L, 2L, 3L, 4L, 5L, 1L, 2L, 3L, 4L, 5L, 1L, 2L, 3L, 4L, 5L, 1L, 2L, 3L, 4L, 5L, 1L, 2L, 3L, 4L, 5L };
        blockchain.put(startRowData, listC);
        assertArrayEquals(listC, blockchain.getLongs(startRowData));

        blockchain.close();
    }

    @Test
    @DisplayName("Blockchain asynchronuous Test")
    public void testAsync() {
        Blockchain blockchain = new Blockchain(MAX_BLOCKS, MAX_ROW_BYTE_LENGTH);

        ThreadLocalRandom rand = ThreadLocalRandom.current();

        // init some data
        List<int[]> data = new ArrayList<>();
        for (int i=0;i<NUM_THREADS;i++) {
            int size = rand.nextInt(35);
            for (int j=0;j<size;j++) {
                int [] x = new int [size];
                for (int k=0;k<size;k++) {
                    x[k] = rand.nextInt(100);
                }
                data.add(x);
            }
        }

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch startSignal = new CountDownLatch(1);
        for (int i = 0; i < NUM_THREADS; i++) {
            final int pos = i;
            executor.submit(() -> {
                try {
                    startSignal.await();
                    long offset = blockchain.allocate();
                    blockchain.put(offset, data.get(pos));

                    int [] r = blockchain.getInts(offset);
                    assertNotNull(r);
                    assertEquals(data.get(pos).length, r.length);

                } catch(Exception e) {
                    fail("Exception in Thread " + pos + " " + e.getLocalizedMessage());
                }
            });
        }
        startSignal.countDown();

        try {
            long start = System.nanoTime();
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            long end = System.nanoTime();
            System.out.println("Excecuted in " + (end - start) + " nsec");
        } catch (InterruptedException e) {
            fail("Exception in testAsync : " + e.getLocalizedMessage());
        }

        blockchain.close();
    }

    @Test
    @DisplayName("Simple Blockchain Persistence Test")
    public void testPersistence() {
        // Value stored in domain objects.
        long startRowData = 0;
        long[] twoRowData = { 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L, 14L, 15L, 16L, 17L, 18L };

        try {
            Path path = getArenaPath(FILENAME);
            Blockchain blockchain = new Blockchain(MAX_BLOCKS, MAX_ROW_BYTE_LENGTH);

            // write some data
            startRowData = blockchain.allocate();
            blockchain.put(startRowData, twoRowData);
            assertArrayEquals(twoRowData, blockchain.getLongs(startRowData));
            blockchain.save(path);

            blockchain.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception in testPersistence save " + e.getLocalizedMessage());
        }

        try {
            Path path = getArenaPath(FILENAME);
            Blockchain blockchain = Blockchain.load(path);
            long[] loaded = blockchain.getLongs(startRowData);
            assertNotNull(loaded);
            assertArrayEquals(twoRowData, loaded);
            blockchain.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail("Exception in testPersistence load " + e.getLocalizedMessage());
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterAll
    public static void clean() {
        Path path = getArenaPath(FILENAME);
        if (path.toFile().exists()) {
            path.toFile().delete();
        }
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

}
