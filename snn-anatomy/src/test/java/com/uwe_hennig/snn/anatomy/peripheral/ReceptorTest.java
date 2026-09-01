/**
 * @(#)ReceptorTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.allocator.ReceptorModelManager;

/**
 * ReceptorTest
 *
 * @author Uwe Hennig
 */
// TODO Enable!
@Disabled
public class ReceptorTest {
    @Test
    @DisplayName("Receptor Matrix Test")
    public void testMatrix() {
        try {
            final int loops = 1_000_000;

            final int receptors = 2;

            final int capacityA = receptors;
            final int numHeadersA = 1;
            final int numRowsA = 3;
            final int numColumnsA = 3;
            final int numSlotsPerCellA = 2;

            final int capacityB = receptors;
            final int numHeadersB = 2;
            final int numRowsB = 4;
            final int numColumnsB = 2;
            final int numSlotsPerCellB = 1;

            ReceptorModelManager manager = ReceptorModelManager.init(receptors);
            assertEquals(receptors, manager.getNumReceptors());
            System.out.println("Receptors : " + manager.getNumReceptors());

            int receptorAId = manager.newReceptor(capacityA, numHeadersA, numRowsA, numColumnsA, numSlotsPerCellA);
            int receptorBId = manager.newReceptor(capacityB, numHeadersB, numRowsB, numColumnsB, numSlotsPerCellB);

            ReceptorView viewA = manager.getRecptorView(receptorAId);
            ReceptorView viewB = manager.getRecptorView(receptorBId);

            // check meta data
            assertEquals(capacityA, viewA.getCapacity());
            assertEquals(numHeadersA, viewA.getNumHeaders());
            assertEquals(numRowsA, viewA.getNumRows());
            assertEquals(numColumnsA, viewA.getNumColumns());
            assertEquals(numSlotsPerCellA, viewA.getNumSlotsPerCell());

            assertEquals(capacityB, viewB.getCapacity());
            assertEquals(numHeadersB, viewB.getNumHeaders());
            assertEquals(numRowsB, viewB.getNumRows());
            assertEquals(numColumnsB, viewB.getNumColumns());
            assertEquals(numSlotsPerCellB, viewB.getNumSlotsPerCell());

            // check header data index 0
            viewA.setIntakeDistance(0, 11f);
            viewB.setIntakeDistance(0, 22f);
            assertEquals(11f, viewA.getIntakeDistance(0), 0.001);
            assertEquals(22f, viewB.getIntakeDistance(0), 0.001);

            // check header data index 1
            viewA.setIntakeDistance(1, 33f);
            viewB.setIntakeDistance(1, 44f);

            assertEquals(33f, viewA.getIntakeDistance(1), 0.001);
            assertEquals(44f, viewB.getIntakeDistance(1), 0.001);

            // check cell data

            int ops = 0;
            long start = System.nanoTime();
            for (int i = 0; i < loops; i++) {
                for (int r = 0; r < receptors; r++) {
                    // set A
                    for (int row = 0; row < viewA.getNumRows(); row++) {
                        for (int col = 0; col < viewA.getNumColumns(); col++) {
                            viewA.setTargetId(r, row, col, ops++);
                        }
                    }
                    // set B
                    for (int row = 0; row < viewB.getNumRows(); row++) {
                        for (int col = 0; col < viewB.getNumColumns(); col++) {
                            viewB.setTargetId(r, row, col, ops++);
                        }
                    }
                }
            }
            long end = System.nanoTime();
            double sec = (end - start) / ops;
            double avgOpsPerSec = ops / sec;

            System.out.println();
            System.out.println("Writing receptor: ");
            System.out.printf("Operations : %,6d%n", ops);
            System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
            System.out.printf("Latency    : %,13.2f ns/op%n", ops / avgOpsPerSec);

            System.out.println();


            ops = 0;
            start = System.nanoTime();
            for (int i = 0; i < loops; i++) {
                for (int r = 0; r < receptors; r++) {
                    // Check A
                    for (int row = 0; row < viewA.getNumRows(); row++) {
                        for (int col = 0; col < viewA.getNumColumns(); col++) {
                            int actual   = viewA.getTargetId(r, row, col);
                            Blackhole.consume(actual);
                            ops++;
                        }
                    }

                    // Check B
                    for (int row = 0; row < viewB.getNumRows(); row++) {
                        for (int col = 0; col < viewB.getNumColumns(); col++) {
                            int actual   = viewB.getTargetId(r, row, col);
                            Blackhole.consume(actual);
                            ops++;
                        }
                    }
                }
            }
            end = System.nanoTime();
            sec = (end - start) / ops;
            avgOpsPerSec = ops / sec;

            System.out.println("Reading receptors: ");
            System.out.printf("Operations : %,6d%n", ops);
            System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
            System.out.printf("Latency    : %,13.2f ns/op%n", ops / avgOpsPerSec);

            System.out.println();


        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in testMatrix: " + e.getLocalizedMessage());
        } finally {
            ReceptorModelManager.instance().close();
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    public final class Blackhole {
        @SuppressWarnings("unused")
        private static int SINK;

        public static void consume(int v) {
            SINK = v;
            if ((v & 0x1) == 0x1) {
                /* noop */
            }
        }

        public static int getSink() {
            return SINK;
        }
    }
}
