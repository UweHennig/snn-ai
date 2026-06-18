/**
 * @(#)TransferBeatTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.time;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * TransferBeatTest
 *
 * @author Uwe Hennig
 */
public class TransferTest {
    private static final AtomicLong holder = new AtomicLong();

    @Test
    @DisplayName("Simple time Test")
    public void testTime() throws Exception {
        // TODO
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));

        holder.set(0);
    }
}
