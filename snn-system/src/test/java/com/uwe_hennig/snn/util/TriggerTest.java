/**
 * @(#)TriggerTest.java
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
 * TriggerTest
 *
 * @author Uwe Hennig
 */
public class TriggerTest {
    private volatile int counter;

    @Test
    @DisplayName("Simple await Trigger test")
    void testAwaitTrigger() {
        counter = 0;
        Trigger trigger = Trigger.of(Duration.ofMillis(500), () -> execute());
        trigger.startWithTotalRuntime(Duration.ofSeconds(2));
        trigger.awaitCompletion();
        System.out.println(counter);
        assertTrue(counter >= 3, "Invalid counter!");
    }


    @Test
    @DisplayName("Simple stop Trigger test")
    void testStopTrigger() {
        counter = 0;
        Trigger trigger = Trigger.of(Duration.ofMillis(500), () -> execute());
        trigger.startWithTotalRuntime(Duration.ofSeconds(2));
        trigger.stop();
        System.out.println(counter);
        assertTrue(counter < 3, "Invalid counter!");
    }


    private void execute() {
        counter++;
    }

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
