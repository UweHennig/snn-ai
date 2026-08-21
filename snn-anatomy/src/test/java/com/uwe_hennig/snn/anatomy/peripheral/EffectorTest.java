/**
 * @(#)EffectorTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
        final int capacity = 1;
        final int length = 1000;
        final int loops = 1_000_000_000;

        EffectorModel model = EffectorModelManager.init(capacity, length).getModel();
        ThreadLocalRandom rand = ThreadLocalRandom.current();

        try {
            model.setTemporalFilterIndex(0, 31415);
            assertEquals(31415, model.getTemporalFilterIndex(0));

            long start = System.nanoTime();
            for (int i = 0; i < loops; i++) {
                int dendritId = rand.nextInt(length);
                model.setRelatedId(0, dendritId, rand.nextInt(9) + 1);
            }
            long end = System.nanoTime();

            double sec = (end - start) / 1_000_000_000.0;
            double avgOpsPerSec = loops / sec;

            System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
            System.out.printf("Latency    : %,13.2f ns/op%n", 1_000_000_000.0 / avgOpsPerSec);
            System.out.println();

            int sum = 0;
            for (int i = 0; i < length; i++) {
                sum += model.getRelatedId(0, i);
                if (i % 10 == 0) {
                    System.out.println();
                }
                System.out.printf(model.getRelatedId(0, i) + " ");
            }
            System.out.println();
            assertTrue(sum > length, "Values are not set!");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in testEffector: " + e.getLocalizedMessage());
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
