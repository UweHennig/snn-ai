/**
 * @(#)MatrixModelTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * MatrixModelTest
 *
 * @author Uwe Hennig
 */
public class MatrixModelTest {

    @Test
    @DisplayName("Simple Matrix test")
    void testSimple() {
        int capacity = 2;
        int numHeaders = 3;
        int numRows = 3;
        int numColumns = 3;
        int numSlotsPerCell = 2;

        MatrixModel matrix = new MatrixModel(capacity, 1_000_000);
        int m1 = matrix.registerMatrix(numHeaders, numRows, numColumns, numSlotsPerCell);
        int m2 = matrix.registerMatrix(numHeaders, numRows, numColumns, numSlotsPerCell);

        assertEquals(capacity, matrix.getCapacity());

        assertEquals(numHeaders, matrix.getNumHeaders(m1));
        assertEquals(numHeaders, matrix.getNumHeaders(m2));

        assertEquals(numRows, matrix.getNumRows(m1));
        assertEquals(numRows, matrix.getNumRows(m2));

        assertEquals(numColumns, matrix.getNumColumns(m1));
        assertEquals(numColumns, matrix.getNumColumns(m2));

        assertEquals(numSlotsPerCell, matrix.getNumSlotsPerCell(m1));
        assertEquals(numSlotsPerCell, matrix.getNumSlotsPerCell(m2));

        System.out.println("Allocated bytes = " + matrix.getByteSize());

        matrix.setHeaderInt(m1, 0, 1);
        assertEquals(1, matrix.getHeaderInt(m1, 0));

        matrix.setHeaderInt(m1, 1, 2);
        assertEquals(2, matrix.getHeaderInt(m1, 1));

        matrix.setHeaderInt(m1, 2, 3);
        assertEquals(3, matrix.getHeaderInt(m1, 2));

        matrix.setHeaderInt(m2, 0, 4);
        assertEquals(4, matrix.getHeaderInt(m2, 0));

        matrix.setHeaderFloat(m2, 1, 5f);
        assertEquals(5f, matrix.getHeaderFloat(m2, 1));

        matrix.setHeaderDouble(m2, 2, 6d);
        assertEquals(6d, matrix.getHeaderDouble(m2, 2));

        matrix.setStatus(m1, 0, 11);
        assertEquals(11, matrix.getStatus(m1));

        matrix.setStatus(m2, 0, 22);
        assertEquals(22, matrix.getStatus(m2));

        System.out.println("Status M1: " + matrix.getStatus(m1));
        System.out.println("Status M2: " + matrix.getStatus(m2));

        matrix.releaseStatus(m1);
        assertEquals(0, matrix.getStatus(m1));

        matrix.releaseStatus(m2);
        assertEquals(0, matrix.getStatus(m2));

        // check matrix m1
        int counter = 1;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                matrix.setCellInt(m1, row, col, 0, counter);
                matrix.setCellInt(m1, row, col, 1, counter);
                counter++;
            }
        }

        counter = 1;
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numColumns; col++) {
                int c = matrix.getCellInt(m1, row, col, 0);
                assertEquals(counter, c);
                c = matrix.getCellInt(m1, row, col, 1);
                assertEquals(counter, c);
                counter++;
            }
        }
        matrix.close();
    }

    @Test
    @DisplayName("Variable Matrix test")
    public void testPerformance() {
        try {
            final int loops = 1_000_000;
            final int size = 100_000;
            final int matrices = 2;

            MatrixModel matrix = new MatrixModel(matrices, size);
            int m0 = matrix.registerMatrix(2, 20, 20, 5);

            int numHeaders = matrix.getNumHeaders(m0);
            int numRows = matrix.getNumRows(m0);
            int numCols = matrix.getNumColumns(m0);
            int numSlots = matrix.getNumSlotsPerCell(m0);

            long operations = 0;
            int value =0;
            long start = System.nanoTime();
            for (int l = 0; l < loops; l++) {
                for (int nH = 0; nH < numHeaders; nH++) {
                    matrix.setHeaderInt(m0, nH, value++);
                    operations++;
                    for (int nR = 0; nR < numRows; nR++) {
                        for (int nC = 0; nC < numCols; nC++) {
                            for (int nS = 0; nS < numSlots; nS++) {
                                matrix.setCellInt(m0, nR, nC, nS, value++);
                                operations++;
                            }
                        }
                    }
                }
            }
            long end = System.nanoTime();

            long totalNs = end - start;
            double nsPerOp = (double) totalNs / operations;
            double opsPerSec = 1_000_000_000.0 / nsPerOp;

            System.out.printf("Operations     : %,13d ops%n", operations);
            System.out.printf("Throughput     : %,13.2f ops/s%n", opsPerSec);
            System.out.printf("Latency        : %,6.2f ns/op%n", nsPerOp);

            // TODO Check whether the values have actually been written.


        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("Variable Matrix test")
    public void testVariableLengths() {
        long size = 10000;
        MatrixModel model = new MatrixModel(3, size);

        int m0 = model.registerMatrix(1, 1, 1, 1);
        int m1 = model.registerMatrix(5, 1, 10, 1);
        int m2 = model.registerMatrix(2, 10, 10, 2);

        // Matrix 0
        model.setCellInt(m0, 0, 0, 0, 777);
        model.setHeaderInt(m0, 0, 123);

        // Matrix 1
        for (int c = 0; c < 10; c++) {
            model.setCellInt(m1, 0, c, 0, 1000 + c);
        }
        model.setHeaderInt(m1, 4, 456);

        // Matrix 2
        for (int r = 0; r < 10; r++) {
            for (int c = 0; c < 10; c++) {
                model.setCellInt(m2, r, c, 0, 2000 + (r * 10) + c);
            }
        }

        // Check Matrix 0
        assertEquals(1, model.getNumRows(m0));
        assertEquals(777, model.getCellInt(m0, 0, 0, 0));
        assertEquals(123, model.getHeaderInt(m0, 0));

        // Check Matrix 1
        assertEquals(10, model.getNumColumns(m1));
        assertEquals(1009, model.getCellInt(m1, 0, 9, 0));
        assertEquals(456, model.getHeaderInt(m1, 4));

        // Check Matrix 2
        assertEquals(10, model.getNumRows(m2));
        assertEquals(2099, model.getCellInt(m2, 9, 9, 0));

        model.close();
    }

    @BeforeEach
    void setUp(TestInfo info) {
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
