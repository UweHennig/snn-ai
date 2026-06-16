/**
 * @(#)TransferBeatTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.time;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.util.SnnExecutor;
import com.uwe_hennig.snn.util.SnnTransferservice;
import com.uwe_hennig.snn.util.TransferWorker;

/**
 * TransferBeatTest
 *
 * @author Uwe Hennig
 */
public class TransferTest {
    private static final AtomicLong holder = new AtomicLong();

    @Test
    @DisplayName("Simple time Test")
    public void testThroughput() throws Exception {
        AtomicLong sentCounter = new AtomicLong(0);
        AtomicLong receivedCounter = new AtomicLong(0);

        TransferWorker worker = (stimulusId) -> {
            receivedCounter.incrementAndGet();
            Thread currentThread = Thread.currentThread();
            System.out.println("TargetID: " + stimulusId + " threadId: " + currentThread.threadId());
            SnnTransferservice.transfer(stimulusId + 1000);
        };

        SnnExecutor executor = SnnExecutor.of(1024L, 4, worker);
        SnnTransferservice.of(worker);

        executor.start();

        Thread.sleep(50);

        long durationMs = 1000;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + durationMs;

        System.out.println("Starting high-speed produce...");

        try {
            while (System.currentTimeMillis() < endTime) {
                int id = (int) sentCounter.get();
                SnnTransferservice.transfer(id);
                sentCounter.incrementAndGet();
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in TransferTest: " + e.getLocalizedMessage());
        }

        System.out.println("Produce finished. Draining...");

        long drainTimeout = System.currentTimeMillis() + 2000;
        while (receivedCounter.get() < sentCounter.get() && System.currentTimeMillis() < drainTimeout) {
            Thread.sleep(10);
        }

        executor.stop(1000);
        executor.shutdown();

        System.out.println("Sent: " + sentCounter.get());
        System.out.println("Received: " + receivedCounter.get());

        assertTrue(sentCounter.get() > 0, "No data sent!");
        assertEquals(sentCounter.get(), receivedCounter.get(), "Data loss detected!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));

        holder.set(0);
    }
}
