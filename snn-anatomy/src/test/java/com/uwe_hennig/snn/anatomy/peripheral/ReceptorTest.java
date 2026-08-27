/**
 * @(#)ReceptorTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.allocator.ReceptorModelManager;

/**
 * ReceptorTest
 *
 * @author Uwe Hennig
 */
public class ReceptorTest {
    @Test
    @DisplayName("Receptor Matrix Test")
    public void testMatrix() {
        try {
            final int capacity = 2;
            final int rows = 10;
            final int columns = 10;
            final int loops = 1_000_000;

            ReceptorModel model = ReceptorModelManager.init(capacity, rows, columns).getModel();

            ThreadLocalRandom rand = ThreadLocalRandom.current();

            System.out.println("Matrices " + capacity);
            System.out.printf(Locale.ENGLISH, "rows = %d columns= %d%n", rows, columns);
            System.out.println("Test loops  : " + loops);

            long ops = 0L;
            long start = System.nanoTime();
            for (int i = 0; i < loops; i++) {
                for (int index = 0; index < capacity; index++) {
                    float val = rand.nextFloat(1, 10);
                    model.setIntakeDistance(index, val);
                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < columns; col++) {
                            int data = rand.nextInt(1, 100);
                            model.setTargetId(index, row, col, data);
                            model.setTargetType(index, row, col, data);
                            ops++;
                        }
                    }
                }
            }
            long end = System.nanoTime();
            double sec = (end - start) / ops;
            double avgOpsPerSec = ops / sec;

            System.out.println();

            System.out.println("Filling matrix: ");
            System.out.printf("Operations : %,6d%n", ops);
            System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
            System.out.printf("Latency    : %,13.2f ns/op%n", ops / avgOpsPerSec);

            System.out.println();

            ops = 0;
            start = System.nanoTime();
            for (int i = 0; i < loops; i++) {
                for (int index = 0; index < capacity; index++) {
                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < columns; col++) {
                            int id  = model.getTargetId(index, row, col);
                            int type  = model.getTargetType(index, row, col);
                            Blackhole.consume(id);
                            Blackhole.consume(type);
                            ops++;
                        }
                    }
                }
            }
            end = System.nanoTime();
            sec = (end - start) / ops;
            avgOpsPerSec = ops / sec;

            System.out.println();
            System.out.println("Reading matrix: ");
            System.out.printf("Operations : %,6d%n", ops);
            System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
            System.out.printf("Latency    : %,13.2f ns/op%n", 1_000_000_000.0 / avgOpsPerSec);

            System.out.println();

            int maxRows = Math.min(rows, 5);
            int maxCols = Math.min(rows, 5);
            for (int index = 0; index < capacity; index++) {
                System.out.println("Matrix " + (index + 1));
                for (int row = 0; row < maxRows; row++) {
                    for (int col = 0; col < maxCols; col++) {
                        int type  = model.getTargetType(index, row, col);
                        int id  = model.getTargetId(index, row, col);
                        System.out.printf(Locale.ENGLISH, "(%2d, %2d) ", id, type);
                        assertTrue(type >= 1 && type < 100, "Invalid type value in Receptor");
                        assertTrue(id >= 1 && id < 100, "Invalid type value in Receptor");
                    }
                    System.out.println("...");
                }
                System.out.println("...");
            }

            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in testMatrix: " + e.getLocalizedMessage());
        } finally {
            ReceptorModelManager.close();
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
            if ((v & 0x1) == 0x1) { /* noop */ }
        }
    }

}
