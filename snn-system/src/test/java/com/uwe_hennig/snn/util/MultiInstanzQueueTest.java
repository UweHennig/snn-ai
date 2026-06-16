/**
 * @(#)MultiInstanzQueueTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * MultiInstanzQueueTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class MultiInstanzQueueTest {
    private IntQueue queueP;
    private IntQueue queueC;

    @BeforeEach
    void setUp(TestInfo info) {
        queueP = new IntQueue(65536);
        queueC = new IntQueue(65536);

        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterEach
    void tearDown() {
        queueP.close();
        queueC.close();
    }

    @Test
    @DisplayName("Simple MultiInstanzQueueTest test")
    void testSimple() {
        for (int i=0;i<70_000;i+=2) {
            queueP.offer(i);
            queueC.offer(i+1);

            int p = queueP.poll();
            int c = queueC.poll();

            assertEquals(i, p, "Invalid value in queue P");
            assertEquals(i+1, c, "Invalid value in queue C");
        }
    }
}
