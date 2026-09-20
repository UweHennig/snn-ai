/// @(#)BufferedTransferTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.util;

import static com.uwe_hennig.snn.util.BufferedTransferSegment.EMTPY_VALUE;
import static com.uwe_hennig.snn.util.BufferedTransferSegment.ENTRY_SIZE;
import static com.uwe_hennig.snn.util.BufferedTransferSegment.META_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/// BufferedTransferTest
///
/// @author Uwe Hennig
public class BufferedTransferTest {
    @Test
    @DisplayName("BufferedTransfer data test")
    public void testData() {
        final int srcId = 10;
        final int srcType = 20;

        BufferedTransferSegment ts = new BufferedTransferSegment(1048576);

        int blockOffset = ts.allocateBlock(1, srcId, srcType);
        assertEquals(META_SIZE, blockOffset, "invalid start of initial block");
        int value = ts.getNumBlocks();
        assertEquals(1, value, "invalid num blocks");

        value = ts.getStartOffset();
        assertEquals(META_SIZE, value, "invalid stored start offset");

        value = ts.getEntrySize();
        assertEquals(ENTRY_SIZE, value, "invalid stored entry size");

        value = ts.getSrcId(blockOffset);
        assertEquals(srcId, value, "invalid stored source id");

        value = ts.getSrcType(blockOffset);
        assertEquals(srcType, value, "invalid stored source type");

        ts.close();
    }

    @Test
    @DisplayName("BufferedTransfer data test")
    public void testSimpleEntry() {
        final int srcId = 10;
        final int srcType = 20;

        BufferedTransferSegment ts = new BufferedTransferSegment(1048576);
        int blockOffset = ts.allocateBlock(2, srcId, srcType);

        ts.setTrgId(blockOffset, 0, 30);
        ts.setTrgId(blockOffset, 1, 40);

        ts.setTrgType(blockOffset, 0, 50);
        ts.setTrgType(blockOffset, 1, 60);

        int value;
        value = ts.getTrgId(blockOffset, 0);
        assertEquals(30, value, "invalid target identifier for entry 0");

        value = ts.getTrgId(blockOffset, 1);
        assertEquals(40, value, "invalid target identifier for entry 1");

        value = ts.getTrgType(blockOffset, 0);
        assertEquals(50, value, "invalid target type for entry 0");

        value = ts.getTrgType(blockOffset, 1);
        assertEquals(60, value, "invalid target type for entry 1");

        ts.close();
    }

    @Test
    @DisplayName("BufferedTransfer simple entry rotation test")
    public void testSimpleRotation() {
        System.setProperty("snn.logging", "true");
        final int srcId = 10;
        final int srcType = 20;
        BufferedTransferSegment ts = new BufferedTransferSegment(1048576);
        int blockOffset = ts.allocateBlock(1, srcId, srcType);
        try {
            Queue<Float> fbTimeQ = new LinkedList<>();
            Queue<Float> fbValueQ = new LinkedList<>();
            Queue<Float> stimulusQ = new LinkedList<>();

            for (int i = 0; i < 3; i++) {
                ts.offerFbTime(blockOffset, 0, 10f); fbTimeQ.offer(10f);
                ts.offerFbTime(blockOffset, 0, 20f); fbTimeQ.offer(20f);
                ts.offerFbTime(blockOffset, 0, 30f); fbTimeQ.offer(30f);
                assertEquals(fbTimeQ.poll(), ts.pollFbTime(blockOffset, 0));
                assertEquals(fbTimeQ.poll(), ts.pollFbTime(blockOffset, 0));
                assertEquals(fbTimeQ.poll(), ts.pollFbTime(blockOffset, 0));

                ts.offerFbValue(blockOffset, 0, 40f);
                fbValueQ.offer(40f);
                ts.offerFbValue(blockOffset, 0, 50f);
                fbValueQ.offer(50f);
                ts.offerFbValue(blockOffset, 0, 60f);
                fbValueQ.offer(60f);
                assertEquals(fbValueQ.poll(), ts.pollFbValue(blockOffset, 0));

                ts.offerStimulus(blockOffset, 0, 70f);
                stimulusQ.offer(70f);
                ts.offerStimulus(blockOffset, 0, 80f);
                stimulusQ.offer(80f);
                ts.offerStimulus(blockOffset, 0, 90f);
                stimulusQ.offer(90f);

                assertEquals(stimulusQ.poll(), ts.pollStimulus(blockOffset, 0));
                assertEquals(stimulusQ.poll(), ts.pollStimulus(blockOffset, 0));

                assertEquals(fbValueQ.poll(), ts.pollFbValue(blockOffset, 0));
                assertEquals(fbValueQ.poll(), ts.pollFbValue(blockOffset, 0));
                assertEquals(stimulusQ.poll(), ts.pollStimulus(blockOffset, 0));
            }
            assertEquals(EMTPY_VALUE, ts.pollFbTime(blockOffset, 0));
            assertEquals(EMTPY_VALUE, ts.pollFbValue(blockOffset, 0));
            assertEquals(EMTPY_VALUE, ts.pollStimulus(blockOffset, 0));
            System.setProperty("snn.logging", "false");
        } finally {
            if (ts != null) {
                ts.close();
            }
        }
    }

    @Test
    @DisplayName("BufferedTransfer simple entry rotation test")
    public void testPerformance() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        final int entries = 100;
        final int srcId = 10;
        final int srcType = 20;

        BufferedTransferSegment ts = new BufferedTransferSegment(1048576);
        int blockOffset = ts.allocateBlock(entries, srcId, srcType);

        int entry = 0;
        int method = 0;

        long operations = 0L;
        long start = System.nanoTime();
        for (int i = 0; i < 10_000_000; i++) {
            entry = rand.nextInt(entries);
            method = i % 3;
            switch (method) {
                case 0:
                    ts.offerStimulus(blockOffset, entry, i % 10);
                break;
                case 1:
                    ts.offerFbTime(blockOffset, entry, i % 20);
                break;
                case 2:
                    ts.offerFbValue(blockOffset, entry, i % 30);
                break;
            }
            operations++;
        }
        long end = System.nanoTime();
        printPerformance("Write entries", operations, end - start);

        operations = 0L;
        start = System.nanoTime();
        for (int i = 0; i < 10_000_000; i++) {
            entry = rand.nextInt(entries);
            method = i % 3;
            switch (method) {
                case 0:
                    Blackhole.consume(ts.pollStimulus(blockOffset, entry));
                break;
                case 1:
                    Blackhole.consume(ts.pollFbTime(blockOffset, entry));
                break;
                case 2:
                    Blackhole.consume(ts.pollFbValue(blockOffset, entry));
                break;
            }
            operations++;
        }
        end = System.nanoTime();
        printPerformance("Read entries", operations, end - start);

        ts.close();
    }

    @Test
    @DisplayName("BufferedTransfer Async Worst-Case Random Scatter-Shot")
    public void testAsync() {
        System.setProperty("snn.logging", "true");
        final int numThreads = 2;
        final int entries = 50;
        final int blocks = 5;
        final int duration = 10;

        BufferedTransferSegment model = new BufferedTransferSegment(1024*1024);
        int[] blockArray = new int[blocks];

        for (int i = 0; i < blocks; i++) {
            blockArray[i] = model.allocateBlock(entries, i, i);
        }

        AtomicBoolean running = new AtomicBoolean(true);
        AtomicLong writeOp = new AtomicLong();
        AtomicLong readOp = new AtomicLong();
        AtomicLong readMiss= new AtomicLong();
        AtomicLong dropMiss= new AtomicLong();

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        //ExecutorService executor = Executors.newFixedThreadPool(2);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                ThreadLocalRandom rand = ThreadLocalRandom.current();
                while (running.get()) {
                    int randBlock = rand.nextInt(blockArray.length);
                    int randEntry = rand.nextInt(entries);
                    int block = blockArray[randBlock];

                    boolean result = model.offerStimulus(block, randEntry, rand.nextFloat());
                    writeOp.incrementAndGet();

                    if (!result) {
                        dropMiss.incrementAndGet();
                    } else {
                        float value = model.pollStimulus(block, randEntry);
                        Blackhole.consume(value);
                        if (Float.isNaN(value)) {
                            readMiss.incrementAndGet();
                        }
                        readOp.incrementAndGet();
                    }
                }
            });
        }
        Blackhole.end();

        long totalWriteOps = 0;
        long totalReadOps = 0;
        long totalMisses = 0;

        long totalDropMiss = 0;
        long totalReadMiss = 0;

        try {
            for (int sec = 1; sec <= duration; sec++) {
                Thread.sleep(1000);

                totalWriteOps += writeOp.getAndSet(0);
                totalReadOps += readOp.getAndSet(0);
                totalDropMiss += dropMiss.getAndSet(0);
                totalReadMiss += readMiss.getAndSet(0);
                totalMisses = totalDropMiss + totalReadMiss;

                System.out.printf("\tSecond %,d: %,11d reads%n", sec, totalReadOps);
                System.out.printf("\tSecond %,d: %,11d writes%n", sec, totalWriteOps);
                System.out.printf("\tSecond %,d: %,11d misses%n%n", sec, totalMisses);
            }
            running.set(false);
            executor.shutdown();
            executor.awaitTermination(1, TimeUnit.SECONDS);
            long totalOps = totalWriteOps + totalReadOps;
            double avgOpsPerSec = (double) totalOps / duration;

            System.out.println();
            System.out.printf("Reads      : %,11d (%,3.2f%%)%n", totalReadOps, 100f * totalReadOps / totalReadOps);
            System.out.printf("Writes     : %,11d (%,3.2f%%)%n", totalWriteOps, 100f * totalWriteOps / totalReadOps);
            System.out.printf("Drop Misses: %,11d (%,3.2f%%)%n", totalDropMiss, 100f * totalDropMiss / totalOps);
            System.out.printf("Read Misses: %,11d (%,3.2f%%)%n", totalReadMiss, 100f * totalReadMiss / totalOps);

            System.out.printf("Throughput : %,14.2f ops/sec%n", avgOpsPerSec / 2f);
            System.out.printf("Latency    : %,14.2f ns/op%n", (1_000_000_000f / avgOpsPerSec) / 2f);

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        } finally {
            System.setProperty("snn.logging", "false");
            model.close();
        }
    }

    public final class Blackhole {
        private static long         liveness;
        public static volatile long SINK;

        public static void consume(float f) {
            liveness += Float.floatToRawIntBits(f);
        }

        public static void consume(boolean b) {
            liveness += 1;
        }

        public static void end() {
            SINK = liveness;

            if (SINK == System.nanoTime()) {
                System.out.print("This will almost never happen" + SINK);
            }

            liveness = 0L;
        }
    }

    void printPerformance(String info, long operations, long deltaT) {
        double nsPerOp = (double) deltaT / operations;
        double opsPerSec = 1_000_000_000.0 / nsPerOp;
        System.out.println();
        System.out.println(info);
        System.out.println("----------------------------------------");
        System.out.printf("Operations     : %,10d.00 ops%n", operations);
        System.out.printf("Throughput     : %,13.2f ops/s%n", opsPerSec);
        System.out.printf("Latency        : %,13.2f ns/op%n", nsPerOp);
    }

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
