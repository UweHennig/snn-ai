/// @(#)BufferedTransferTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.util;

import static com.uwe_hennig.snn.util.BufferedTransferSegment.BLOCK_SIZE;
import static com.uwe_hennig.snn.util.BufferedTransferSegment.EMTPY_VALUE;
import static com.uwe_hennig.snn.util.BufferedTransferSegment.ENTRY_SIZE;
import static com.uwe_hennig.snn.util.BufferedTransferSegment.META_SIZE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.ThreadLocalRandom;

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

        value = ts.getBlockSize();
        assertEquals(BLOCK_SIZE, value, "invalid stored block size");

        value = ts.getEntrySize();
        assertEquals(ENTRY_SIZE, value, "invalid stored entry size");

        value = ts.getEndOffset();
        assertEquals(META_SIZE + BLOCK_SIZE + ENTRY_SIZE, value, "invalid stored end offset");

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
        final int srcId = 10;
        final int srcType = 20;

        BufferedTransferSegment ts = new BufferedTransferSegment(1048576);
        int blockOffset = ts.allocateBlock(1, srcId, srcType);

        assertEquals(EMTPY_VALUE, ts.pollFbTime(blockOffset, 0));

        ts.offerFbTime(blockOffset, 0, 10f);
        assertEquals(10f, ts.pollFbTime(blockOffset, 0));
        assertEquals(EMTPY_VALUE, ts.pollFbTime(blockOffset, 0));

        ts.offerFbTime(blockOffset, 0, 10f);
        ts.offerFbTime(blockOffset, 0, 20f);
        assertEquals(10f, ts.pollFbTime(blockOffset, 0));
        assertEquals(20f, ts.pollFbTime(blockOffset, 0));
        assertEquals(EMTPY_VALUE, ts.pollFbTime(blockOffset, 0));

        ts.offerFbTime(blockOffset, 0, 10f);
        ts.offerFbTime(blockOffset, 0, 20f);
        ts.offerFbTime(blockOffset, 0, 30f);
        assertEquals(10f, ts.pollFbTime(blockOffset, 0));
        assertEquals(20f, ts.pollFbTime(blockOffset, 0));
        assertEquals(30f, ts.pollFbTime(blockOffset, 0));
        assertEquals(EMTPY_VALUE, ts.pollFbTime(blockOffset, 0));

        ts.close();
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

    public final class Blackhole {
        private static long         liveness;
        public static volatile long SINK;

        public static void consume(float f) {
            liveness += Float.floatToRawIntBits(f);
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
