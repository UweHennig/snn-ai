/**
 * @(#)SynapseChainModelTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * SynapseChainModelTest
 *
 * @author Uwe Hennig
 */
public class SynapseChainModelTest {

    @Test
    @DisplayName("SynapseChainModel single Block Test")
    public void testSingleBlock() {
        SynapseChainModel list = new SynapseChainModel(1024);

        int offset = list.allocate(4);

        list.addSynapseId(offset, 10);
        list.addSynapseId(offset, 20);
        list.addSynapseId(offset, 30);

        int[] syn = list.getSynapses(offset);

        assertArrayEquals(new int[] { 10, 20, 30 }, syn);
    }

    @Test
    @DisplayName("SynapseChainModel two Blocks Test")
    public void testTwoBlocks() {
        SynapseChainModel list = new SynapseChainModel(2048);

        int offset = list.allocate(2);

        list.addSynapseId(offset, 1);
        list.addSynapseId(offset, 2);
        list.addSynapseId(offset, 3); // triggers recursion

        int[] syn = list.getSynapses(offset);

        assertArrayEquals(new int[] { 1, 2, 3 }, syn);
    }

    @Test
    @DisplayName("SynapseChainModel multi Blocks Test")
    public void testMultiBlocks() {
        SynapseChainModel list = new SynapseChainModel(4096);

        int offset = list.allocate(3);

        for (int i = 0; i < 10; i++) {
            list.addSynapseId(offset, i);
        }

        int[] syn = list.getSynapses(offset);

        assertArrayEquals(new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8, 9 }, syn);
    }

    @Test
    @DisplayName("SynapseChainModel next Block terminator Test")
    public void testNextBlockTerminator() {
        SynapseChainModel list = new SynapseChainModel(1024);

        int offset = list.allocate(1);

        list.addSynapseId(offset, 42);

        assertEquals(-1, list.getNextBlock(offset));
    }

    @Test
    @DisplayName("SynapseChainModel Performance Test")
    public void testPerformance() {
        final int it = 1024;

        SynapseChainModel model = null;
        try {
            model = new SynapseChainModel(it * it * it);
            int chainOffset = model.allocate(100);
            long operations = 0L;

            long start = System.nanoTime();
            for (int o = 0; o < it; o++) {
                int offset = model.allocate(it);
                model.addSynapseId(chainOffset, offset);
                operations++;

                for (int id = 0; id < it; id++) {
                    model.addSynapseId(offset, id);
                    operations++;
                }
            }
            long end = System.nanoTime();

            long totalNs = end - start;
            double nsPerOp = (double) totalNs / operations;
            double opsPerSec = 1_000_000_000.0 / nsPerOp;

            printPerformenceResult("Writing:", operations, nsPerOp, opsPerSec);

            operations = 0L;
            start = System.nanoTime();
            int[] offsets = model.getFirstList();
            operations+=offsets.length;
            for (int i = 0; i < offsets.length; i++) {
                int [] snapses = model.getSynapses(offsets[i]);
                Blackhole.consume(snapses);
                operations+=snapses.length;
            }
            end = System.nanoTime();
            totalNs = end - start;
            nsPerOp = (double) totalNs / operations;
            opsPerSec = 1_000_000_000.0 / nsPerOp;

            printPerformenceResult("Reading:", operations, nsPerOp, opsPerSec);

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        } finally {
            if (model != null) {
                model.close();
            }
        }
    }

    @Test
    @DisplayName("SynapseChainModel Header Test")
    public void testHeaderIntegrity() {
        SynapseChainModel list = new SynapseChainModel(2048);

        int offset = list.allocate(2);

        list.addSynapseId(offset, 100);
        list.addSynapseId(offset, 200);
        list.addSynapseId(offset, 300);

        assertEquals(2, list.getBlockCapacity(offset));
        assertEquals(2, list.getNumElements(offset));
        assertEquals(3, list.getTotalElements(offset));

        int next = list.getNextBlock(offset);

        assertEquals(1, list.getNumElements(next));
    }

    private void printPerformenceResult(String info, long operations, double nsPerOp, double opsPerSec) {
        System.out.println();
        System.out.println(info);
        System.out.println("----------------------------------------");
        System.out.printf("Operations     : %,10d.00 ops%n", operations);
        System.out.printf("Throughput     : %,13.2f ops/s%n", opsPerSec);
        System.out.printf("Latency        : %,13.2f ns/op%n", nsPerOp);
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    public final class Blackhole {
        private static volatile Object SINK;

        public static void consume(Object v) {
            SINK = v;
        }
    }

}
