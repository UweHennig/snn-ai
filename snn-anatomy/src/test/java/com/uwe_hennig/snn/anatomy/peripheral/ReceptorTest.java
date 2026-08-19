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
        final int bound = 1000;
        ReceptorModel model = ReceptorModelManager.init(bound, bound).getModel();
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        try {
            model.setInformationFilterIndex(1234);
            model.setTemporalFilterIndex(56789);

            long start = System.nanoTime();
            for (int i = 0; i < 100_000_000; i++) {
                int row = rand.nextInt(bound);
                int col = rand.nextInt(bound);
                int val = rand.nextInt(9) + 1;
                model.putDendritId(row, col, val);
            }
            long end = System.nanoTime();
            double sec = (end - start) / 1_000_000_000.0;
            double avgOpsPerSec = 100_000_000.0 / sec;

            System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
            System.out.printf("Latency    : %,13.2f ns/op%n", 1_000_000_000.0 / avgOpsPerSec);

            System.out.println();

            int sum = 0;
            int views = Math.min(bound, 10);
            for (int row = 0; row < views; row++) {
                for (int col = 0; col < views; col++) {
                    int val = model.getDendritId(row, col);
                    sum += val;
                    System.out.print(val + " ");
                }
                System.out.println("...");
            }
            System.out.println("...");
            assertTrue(sum > 100, "Values are not set!");

            assertEquals(1234, model.getInformationFilterIndex());

            assertEquals(bound, model.rows());
            assertEquals(bound, model.columns());

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

}
