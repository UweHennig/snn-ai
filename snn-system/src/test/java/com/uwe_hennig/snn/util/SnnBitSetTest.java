/**
 * @(#)SnnBitSetTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.BitSet;
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * SnnBitSetTest
 *
 * @author Uwe Hennig
 */
public class SnnBitSetTest {
    @Test
    @DisplayName("Simple BitSet test")
    public void testSimpleBitSet() {
        try (SnnBitSet bs = new SnnBitSet(1)) {
            bs.set(3);
            bs.set(1_000_00);
            boolean flag3 = bs.get(3);
            assertEquals(true, flag3);

            boolean falg4 = bs.get(5);
            assertEquals(false, falg4);

            boolean flag1Mio = bs.get(1_000_00);
            assertEquals(true, flag1Mio);

            boolean flag10Mio = bs.get(10_000_00);
            assertEquals(false, flag10Mio);

            assertEquals(2, bs.cardinality(), "Invalid cardinality in testNextBit");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in testSimpleBitSet " + e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("Search for next bit test")
    public void testNextBit() {
        try (SnnBitSet bs = new SnnBitSet(100)) {
            for (long i = 10; i < 100; i += 10) {
                bs.set(i);
            }
            long pos = 0;
            long expected = 10;
            while (true) {
                pos = bs.nextBit(pos);
                if (pos < 0) {
                    break;
                }
                System.out.println("Next bit position: " + pos);
                assertEquals(expected, pos, "Invalid position from nextSetBitPosition " + pos);
                pos++;
                expected += 10;
            }

            assertEquals(9, bs.cardinality(), "Invalid cardinality in testNextBit");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in testSimpleBitSet " + e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("Find highest set bit test")
    public void testHighestBit() {
        try (SnnBitSet bs = new SnnBitSet(100)) {
            bs.set(95);
            bs.set(90);
            long highest = bs.highestBit();
            assertEquals(95L, highest, "Invalid highest bit!");
            int cardinality = bs.cardinality();
            assertEquals(2, cardinality, "Invalid cardinality in testHighestBit");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in testHighestBit " + e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("Simple SnnMultiBitSet test")
    public void testMultiBitSet() {
        final int numFields = 2;
        final int numTypes = 2;

        try (SnnMultiBitSet mbs = new SnnMultiBitSet(numFields, numTypes)) {
            mbs.set(0, 0, 1);
            checkValue(mbs, true, 0, 0, 1);

            mbs.set(0, 1, 2);
            checkValue(mbs, true, 0, 1, 2);

            mbs.set(1, 0, 3);
            checkValue(mbs, true, 1, 0, 3);

            mbs.set(1, 1, 4);
            checkValue(mbs, true, 1, 1, 4);

            mbs.unset(0, 0, 1);
            checkValue(mbs, false, 0, 0, 1);
            checkValue(mbs, true, 0, 1, 2);
            checkValue(mbs, true, 1, 0, 3);
            checkValue(mbs, true, 1, 1, 4);

            mbs.unset(0, 1, 2);
            checkValue(mbs, false, 0, 1, 2);
            checkValue(mbs, true, 1, 0, 3);
            checkValue(mbs, true, 1, 1, 4);

            mbs.unset(1, 0, 3);
            checkValue(mbs, false, 1, 0, 3);
            checkValue(mbs, true, 1, 1, 4);

            mbs.unset(1, 1, 4);
            checkValue(mbs, false, 1, 1, 4);

            assertEquals(0, mbs.cardinality(), "Invalid cardinality in testMultiBitSet " + mbs.cardinality());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in testMultiBitSet " + e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("Simple performance test")
    public void testPerformance() {
        Random rand = new Random(System.currentTimeMillis());
        int iterations = 10_000_000;
        int bitCount = 1_000_000;

        try (SnnBitSet bs = new SnnBitSet(1_000_000)) {
            // Warmup
            for (int i = 0; i < 1_000; i++) {
                int pos = rand.nextInt(1_000);
                if (bs.get(pos)) {
                    bs.unset(pos);
                } else {
                    bs.set(pos);
                }
            }
            // Measurement
            long start = System.nanoTime();
            for (int i = 0; i < iterations; i++) {
                int pos = rand.nextInt(bitCount);
                if (bs.get(pos)) {
                    bs.unset(pos);
                } else {
                    bs.set(pos);
                }
            }
            long end = System.nanoTime();

            long totalNs = end - start;
            double nsPerOp = (double) totalNs / iterations;
            double opsPerSec = 1_000_000_000.0 / nsPerOp;

            System.out.println("--- SnnBitSet ---");
            System.out.printf("Total duration : %,d ms%n", totalNs / 1_000_000);
            System.out.printf("Latency        : %,6.2f ns/op%n", nsPerOp);
            System.out.printf("Throughput     : %,13.2f ops/s%n", opsPerSec);
        }

        // Java BitSet
        BitSet jbs = new BitSet();
        // Warumup
        for (int i = 0; i < 1_000; i++) {
            int pos = rand.nextInt(1_000);
            if (jbs.get(pos)) {
                jbs.clear(pos);
            } else {
                jbs.set(pos);
            }
        }
        // Measurement
        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            int pos = rand.nextInt(bitCount);
            if (jbs.get(pos)) {
                jbs.clear(pos);
            } else {
                jbs.set(pos);
            }
        }
        long end = System.nanoTime();

        long totalNs = end - start;
        double nsPerOp = (double) totalNs / iterations;
        double opsPerSec = 1_000_000_000.0 / nsPerOp;

        System.out.println("--- JavaBitSet ---");
        System.out.printf("Total duration : %,d ms%n", totalNs / 1_000_000);
        System.out.printf("Latency        : %,6.2f ns/op%n", nsPerOp);
        System.out.printf("Throughput     : %,13.2f ops/s%n", opsPerSec);
    }

    private void checkValue(SnnMultiBitSet mbs, boolean expected, int field, int type, int index) {
        boolean result = mbs.get(field, type, index);
        assertEquals(expected, result, "invalid value in SnnMultiBitSet!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
