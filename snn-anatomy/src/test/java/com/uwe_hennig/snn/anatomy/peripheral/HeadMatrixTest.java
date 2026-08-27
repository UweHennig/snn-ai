/**
 * @(#)HeadMatrixTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.Test;

import com.uwe_hennig.snn.anatomy.peripheral.ReceptorTest.Blackhole;

/**
 * HeadMatrixTest
 *
 * @author Uwe Hennig
 */
public class HeadMatrixTest {
    public static final class ReceptorPool implements AutoCloseable {
        private static final VarHandle VH_INT   = ValueLayout.JAVA_INT.withByteAlignment(4).varHandle();
        private static final VarHandle VH_FLOAT = ValueLayout.JAVA_FLOAT.withByteAlignment(4).varHandle();

        private static final long HEADER_SIZE = 8;
        private static final long TARGET_SIZE = 8;

        private final int  numReceptors;
        private final long rowSize;      // Bytes pro Zeile in der Matrix
        private final long receptorSize; // sizeof([HEADER_DATA][MATRIX])

        private Arena         arena;
        private MemorySegment segment;

        public ReceptorPool(int numReceptors, int rows, int columns) {
            this.numReceptors = numReceptors;
            this.rowSize = columns * TARGET_SIZE;
            this.receptorSize = HEADER_SIZE + (rows * rowSize);

            this.arena = Arena.ofShared();
            this.segment = arena.allocate(numReceptors * receptorSize);
        }

        public void setTargetId(int index, int row, int col, int value) {
            long receptorIdx = index * receptorSize;
            long matrixStart = receptorIdx + HEADER_SIZE;
            long offsetTargetId = matrixStart + (row * rowSize) + (col * TARGET_SIZE);

            VH_INT.set(segment, offsetTargetId, value);
        }

        public int getTargetId(int index, int row, int col) {
            long offsetTargetId = (index * receptorSize) + HEADER_SIZE + (row * rowSize) + (col * TARGET_SIZE);
            return (int) VH_INT.get(segment, offsetTargetId);
        }

        public void setTargetType(int index, int row, int col, int value) {
            long receptorIdx = index * receptorSize;
            long matrixStart = receptorIdx + HEADER_SIZE;

            long offset = matrixStart + (row * rowSize) + (col * TARGET_SIZE) + 4;

            VH_INT.set(segment, offset, value);
        }

        public int getTargetType(int index, int row, int col) {
            long offsetTargetType = (index * receptorSize) + HEADER_SIZE + (row * rowSize) + (col * TARGET_SIZE) + 4;
            return (int) VH_INT.get(segment, offsetTargetType);
        }

        public void setIntakeDistance(int index, float value) {
            long offset = (index * receptorSize) + 4;
            VH_FLOAT.set(segment, offset, value);
        }

        public float getIntakeDistance(int index) {
            long offset = (index * receptorSize) + 4;
            return (float) VH_FLOAT.get(segment, offset);
        }

        public int getNumReceptors() {
            return numReceptors;
        }

        @Override
        public void close() throws Exception {
            if (arena != null) {
                arena.close();
                arena = null;
                segment = null;
            }
        }
    }

    @Test
    public void testData() {
        final int capacity = 2;
        final int rows = 10;
        final int columns = 10;
        try (ReceptorPool pool = new ReceptorPool(capacity, rows, columns)) {
            int id = 1;
            // 1. SCHREIBEN
            for (int r = 0; r < pool.getNumReceptors(); r++) {
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        pool.setTargetId(r, row, col, id++);
                        pool.setTargetType(r, row, col, 1);
                    }
                }
            }

            // 2. LESEN
            for (int r = 0; r < pool.getNumReceptors(); r++) {
                System.out.println("Receptor " + r);
                for (int row = 0; row < rows; row++) {
                    for (int col = 0; col < columns; col++) {
                        int tId = pool.getTargetId(r, row, col);
                        int tType = pool.getTargetType(r, row, col);

                        System.out.printf("(%3d, %3d) ", tId, tType);
                        assertTrue(tId > 0, "ID sollte > 0 sein bei " + row + "/" + col);
                    }
                    System.out.println();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception : " + e.getLocalizedMessage());
        }
    }

    @Test
    public void testPerformance() {
        final int capacity = 2;
        final int rows = 10;
        final int columns = 10;
        final int loops = 1_000_000;

        try (ReceptorPool pool = new ReceptorPool(capacity, rows, columns)) {
            final ThreadLocalRandom rand = ThreadLocalRandom.current();

            System.out.println("Matrices " + capacity);
            System.out.printf(Locale.ENGLISH, "rows = %d columns= %d%n", rows, columns);
            System.out.println("Test loops  : " + loops);

            long ops = 0L;
            long start = System.nanoTime();
            for (int i = 0; i < loops; i++) {
                float val = rand.nextFloat(1, 10);
                for (int index = 0; index < capacity; index++) {
                    pool.setIntakeDistance(index, val);
                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < columns; col++) {
                            int data = rand.nextInt(0, 10);
                            pool.setTargetId(index, row, col, data);
                            pool.setTargetType(index, row, col, data);
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
            System.out.printf("Latency    : %,13.2f ns/op%n", 1_000_000_000.0 / avgOpsPerSec);

            System.out.println();

            ops = 0;
            start = System.nanoTime();
            for (int i = 0; i < loops; i++) {
                for (int index = 0; index < capacity; index++) {
                    for (int row = 0; row < rows; row++) {
                        for (int col = 0; col < columns; col++) {
                            int type = pool.getTargetType(index, row, col);
                            int id = pool.getTargetId(index, row, col);
                            Blackhole.consume(type);
                            Blackhole.consume(id);
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

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception : " + e.getLocalizedMessage());
        }
    }
}
