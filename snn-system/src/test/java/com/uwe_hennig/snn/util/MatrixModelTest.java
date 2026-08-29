/**
 * @(#)MatrixTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * MatrixTest
 *
 * @author Uwe Hennig
 */
public class MatrixTest {

    @Test
    @DisplayName("Simple Matrix test")
    void testSimple() {
        int capacity = 2;
        int numHeaders = 3;
        int numRows = 3;
        int numColumns = 3;
        int numSlotsPerCell = 2;

        Matrix matrix = new Matrix(capacity, numHeaders, numRows, numColumns, numSlotsPerCell);

        matrix.setHeaderInt(0, 0, 1);
        matrix.setHeaderInt(0, 1, 2);
        matrix.setHeaderInt(0, 2, 3);

        matrix.setHeaderInt(1, 0, 4);
        matrix.setHeaderFloat(1, 1, 5f);
        matrix.setHeaderDouble(1, 2, 6d);

        int counter = 1;
        for (int index = 0; index < capacity; index++) {
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numColumns; col++) {
                    // numSlotsPerCell = 2
                    matrix.setCellInt(index, row, col, 0, counter);
                    matrix.setCellInt(index, row, col, 1, counter);
                    counter++;
                }
            }
        }

        assertEquals(1, matrix.getHeaderInt(0, 0));
        assertEquals(2, matrix.getHeaderInt(0, 1));
        assertEquals(3, matrix.getHeaderInt(0, 2));

        assertEquals( 4, matrix.getHeaderInt(1, 0));
        assertEquals(5f, matrix.getHeaderFloat(1, 1));
        assertEquals(6d, matrix.getHeaderDouble(1, 2));

        counter = 1;
        for (int index = 0; index < capacity; index++) {
            System.out.println("Matrix: " + index);
            for (int row = 0; row < numRows; row++) {
                for (int col = 0; col < numColumns; col++) {
                    // numSlotsPerCell = 2
                    int c1 = matrix.getCellInt(index, row, col, 0);
                    int c2 = matrix.getCellInt(index, row, col, 1);
                    assertEquals(counter, c1);
                    assertEquals(counter, c2);
                    counter++;
                    System.out.printf(Locale.ENGLISH, "(%2d, %2d) ", c1, c2);
                }
                System.out.println();
            }
            System.out.println();
        }

        matrix.close();
    }

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
