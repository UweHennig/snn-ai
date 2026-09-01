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
// TODO
//        final int capacity = 1000;
//        final int rows = 10;
//        final int columns = 10;
//
//        final int loops = 1_000_000_000;
//
//        EffectorModel model = EffectorModelManager.init(capacity, rows, columns).getModel();
//        ThreadLocalRandom rand = ThreadLocalRandom.current();
//
//        try {
//            long operations = 0;
//            long start = System.nanoTime();
//            for (int i = 0; i < loops; i++) {
//                for (int j = 0; j < rows; j++) {
//                    for (int k = 0; k < columns; k++) {
//                        operations++;
//                        int effectorId = rand.nextInt(10, 100);
//                        float value = rand.nextInt(9) + 1.0f;
//                        int index = rand.nextInt(0, capacity);
//                        int row = rand.nextInt(0, rows);
//                        int col = rand.nextInt(0, columns);
//                        model.setValue(index, row, col, value);
//                        model.setPort(index, row, col, effectorId);
//                    }
//                }
//            }
//            long end = System.nanoTime();
//
//            double sec = (end - start) / 1_000_000_000.0;
//            double avgOpsPerSec = loops / sec;
//
//            System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
//            System.out.printf("Latency    : %,13.2f ns/op%n", 1_000_000_000.0 / avgOpsPerSec);
//            System.out.println();
//
//            int sum = 0;
//            for (int i = 0; i < length; i++) {
//                sum += model.getRelatedId(0, i);
//                if (i % 10 == 0) {
//                    System.out.println();
//                }
//                System.out.printf(model.getRelatedId(0, i) + " ");
//            }
//            System.out.println();
//            assertTrue(sum > length, "Values are not set!");
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Exception in testEffector: " + e.getLocalizedMessage());
//        } finally {
//            EffectorModelManager.close();
//        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
