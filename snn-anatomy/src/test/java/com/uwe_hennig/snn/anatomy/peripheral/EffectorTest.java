/**
 * @(#)EffectorTest.java
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

import com.uwe_hennig.snn.anatomy.allocator.EffectorModelManager;

/**
 * EffectorTest
 *
 * @author Uwe Hennig
 */
public class EffectorTest {
    @Test
    @DisplayName("Effector Test")
    public void testEffector() {
        final int bound = 1000;
        final int loops = 1_000_000_000;

        EffectorModel model = EffectorModelManager.init(bound).getModel();
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        try {
            model.setTemporalFilterIndex(31415);
            assertEquals(31415, model.getTemporalFilterIndex());

            long start = System.nanoTime();
            for (int i = 0; i < loops; i++) {
                int index = rand.nextInt(bound);
                model.setRelatedId(index, rand.nextInt(9) + 1);
            }
            long end = System.nanoTime();

            double sec = (end - start) / 1_000_000_000.0;
            double avgOpsPerSec = loops / sec;

            System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
            System.out.printf("Latency    : %,13.2f ns/op%n", 1_000_000_000.0 / avgOpsPerSec);
            System.out.println();

            int sum = 0;
            for (int i = 0; i < bound; i++) {
                sum += model.getRelatedId(i);
                if (i % 10 == 0) {
                    System.out.println();
                }
                System.out.printf(model.getRelatedId(i) + " ");
            }
            System.out.println();
            assertTrue(sum > bound, "Values are not set!");

        } finally {
            EffectorModelManager.close();
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
