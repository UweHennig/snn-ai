/**
 * @(#)TimeTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.services.SnnExecutor;

/**
 * TimeTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class TimeTest {
    @Test
    @DisplayName("Simple Time Test")
    public void simpleTest() {
        try {
            SnnClock snnClock = SnnClock.instance();
            int [] counter = {0};

            snnClock.start();
            System.out.println("Submit tasks...");
            for (int i = 0; i < 10; i++) {
                final int n = i;
                SnnExecutor.submit(() -> {
                    System.out.println("Thread: " + n);
                    sleep(10 * n);
                    counter[0]+=1;
                });
            }
            snnClock.stop();

            try {
                System.out.println("Wait for the tasks to be completed...");
                snnClock.awaitTermination(1, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Waiting interrupted.");
                fail("InterruptedException " + e.getLocalizedMessage());
            }

            assertEquals(10, counter[0], "Not all threads have been processed!");

            System.out.println("done");
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in TimeTest.TimeTest " + e.getLocalizedMessage());
        }
    }

    public static void sleep(long msec) {
        try {
            Thread.sleep(msec);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
