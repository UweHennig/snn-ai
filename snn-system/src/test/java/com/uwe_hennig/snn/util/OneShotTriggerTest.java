/**
 * @(#)OneShotTriggerTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * OneShotTriggerTest
 *
 * @author Uwe Hennig
 */
public class OneShotTriggerTest {

    @Test
    @DisplayName("Simple OneShotTrigger wait test")
    void testSimpleOneShotWait() {
        long start = System.currentTimeMillis();
        OneShotTrigger ost = OneShotTrigger.of(Duration.ofSeconds(1));
        ost.waitOnSignal();
        long end = System.currentTimeMillis();
        System.out.println("" + (end - start) + " msec");
        assertTrue(end - start >= 999, "Invalid wait time!");
    }

    @Test
    @DisplayName("Simple OneShotTrigger run test")
    void testSimpleOneShotExecute() {
        long start = System.currentTimeMillis();
        OneShotTrigger ost = OneShotTrigger.of(Duration.ofSeconds(1));
        long [] end = {0};
        ost.executeOnSignal(() -> end[0]=System.currentTimeMillis());
        System.out.println((end[0] - start)  + " msec");
        assertTrue(end[0] - start >= 999, "Invalid execution time!");
    }

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
