/**
 * @(#)TransferSegmentTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * TransferSegmentTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class TransferSegmentTest {

    @Test
    @DisplayName("TransferSegment Data Test")
    public void testdata() {
        TransferSegment ts = new TransferSegment(2048);

        // --- meta test
        int value;
        value = ts.getNumBlocks();
        assertEquals(0, value, "invalid mum blocks");

        value = ts.getStartOffset();
        assertEquals(20, value, "invalid start offset");

        value = ts.getHeaderSize();
        assertEquals(20, value, "invalid header size");

        value = ts.getEntrySize();
        assertEquals(12, value, "invalid entry size");

        value = ts.getEndOffset();
        assertEquals(40, value, "invalid end offset");

        // --- block test
        int blockOffset = ts.allocateBlock(2);
        assertEquals(40, blockOffset, "invalid allocate block");

        value = ts.getEndOffset();
        assertEquals(64, value, "invalid end offset");

        ts.setState(blockOffset, 1);
        ts.setCount(blockOffset, 2);
        ts.setCapacity(blockOffset, 3);
        ts.setStimulusType(blockOffset, 4);
        ts.setNextBlock(blockOffset, 5);

        value = ts.getStatus(blockOffset);
        assertEquals(1, value, "invalid state");

        value = ts.getCount(blockOffset);
        assertEquals(2, value, "invalid count");

        value = ts.getCapacity(blockOffset);
        assertEquals(3, value, "invalid capacity");

        value = ts.getStimulusType(blockOffset);
        assertEquals(4, value, "invalid stimulus type");

        value = ts.getNextBlock(blockOffset);
        assertEquals(5, value, "invalid next block");

        // --- entry test
        ts.setEntry(blockOffset, 0, 6, 7, 8.0f);
        ts.setEntry(blockOffset, 1, 9, 10, 11.0f);

        value = ts.getTargetId(blockOffset, 0);
        assertEquals(6, value, "invalid target id at 0");

        value = ts.getTargetId(blockOffset, 1);
        assertEquals(9, value, "invalid target id at 1");

        value = ts.getTargetType(blockOffset, 0);
        assertEquals(7, value, "invalid target type at 0");

        value = ts.getTargetType(blockOffset, 1);
        assertEquals(10, value, "invalid target tpye at 0");

        float fValue = ts.getValue(blockOffset, 0);
        assertEquals(8f, fValue, "invalid value at 0");

        fValue = ts.getValue(blockOffset, 1);
        assertEquals(11f, fValue, "invalid value at 1");

        ts.close();
    }

    @Test
    @DisplayName("TransferSegment Data Test")
    public void testStatus() {
        TransferSegment ts = new TransferSegment(2048);
        int blockOffset = ts.allocateBlock(1);

        boolean value = ts.changeStateToWriting(blockOffset);
        assertTrue(value, "invalid status writing");

        value = ts.changeStateToPublished(blockOffset);
        assertTrue(value, "invalid status published");

        value = ts.changeStateTpReading(blockOffset);
        assertTrue(value, "invalid status reading");

        value = ts.changeStateToAvailable(blockOffset);
        assertTrue(value, "invalid status available");

        value = ts.changeStateToPublished(blockOffset);
        assertFalse(value, "invalid status published");

        value = ts.changeStateTpReading(blockOffset);
        assertFalse(value, "invalid status reading");

        value = ts.changeStateToAvailable(blockOffset);
        assertFalse(value, "invalid status available");

        ts.close();
    }

    @Test
    @DisplayName("TransferSegment Performance Test")
    public void testPerformance() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        TransferSegment ts = new TransferSegment(2048 * 2048);
        int block = ts.allocateBlock(100);

        long operations = 0L;
        int trgId = 0;
        int trgType = 0;
        float value =0F;

        long start = System.nanoTime();
        for (int i = 0; i < 10_000_000; i++) {
            int pos = rand.nextInt(100);
            ts.setEntry(block, pos, trgId++, trgType, value+=i);
            operations++;
        }
        long end = System.nanoTime();
        printPerformance("Writing Entries", operations, end-start);

        ts.close();
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

    public final class Blackhole {
        private static long         liveness;
        public static volatile long SINK;

        public static void consume(Object obj) {
            if (obj != null) {
                liveness += System.identityHashCode(obj);
            }
        }

        public static void end() {
            SINK = liveness;

            if (SINK == System.nanoTime()) {
                System.out.print("This will almost never happen" + SINK);
            }

            liveness = 0L;
        }
    }

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
