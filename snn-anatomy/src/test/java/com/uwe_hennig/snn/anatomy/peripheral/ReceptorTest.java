/**
 * @(#)ReceptorTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * ReceptorTest
 *
 * @author Uwe Hennig
 */
public class ReceptorTest {

    @Test
    @DisplayName("Receptor Matrix Test")
    public void testMatrix() {
        final int bound = 10;
        ReceptorModel model = new ReceptorModel(bound, bound);
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        try {
            model.setInformationFilterIndex(1234);
            model.setTemporalFilterIndex(56789);

            for (int i = 0; i < 1000; i++) {
                int row = rand.nextInt(bound);
                int col = rand.nextInt(bound);
                int val = rand.nextInt(bound);
                model.putDendritId(row, col, val);
            }
            System.out.println();

            int sum = 0;
            for (int row = 0; row < bound; row++) {
                for (int col = 0; col < bound; col++) {
                    int val = model.getDendritId(row, col);
                    sum += val;
                    System.out.print(val + " ");
                }
                System.out.println();
            }
            System.out.println();
            assertTrue(sum > 0, "Values are not set!");

            assertEquals(1234, model.getInformationFilterIndex());

            assertEquals(bound, model.rows());
            assertEquals(bound, model.columns());

        } finally {
            model.close();
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
