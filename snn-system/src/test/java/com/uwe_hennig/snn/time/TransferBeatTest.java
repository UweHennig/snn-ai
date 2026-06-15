/**
 * @(#)TransferBeatTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.time;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.util.SnnExecutor;
import com.uwe_hennig.snn.util.SnnTransferservice;
import com.uwe_hennig.snn.util.TransferWorker;
import com.uwe_hennig.snn.util.TransferWorkerImpl;

/**
 * TransferBeatTest
 * @author Uwe Hennig
 */
public class TransferBeatTest {

    @Test
    @DisplayName("Simple time Test")
    public void testTime() throws Exception {
        // long timeWindow, long minSize, long size
        SnnClockImpl clock = SnnClockImpl.of(1000L, 100L, 100L);
        TransferWorker worker = new TransferWorkerImpl();
        SnnExecutor executor = SnnExecutor.of(1024L, 4, worker);
        SnnTransferservice.of(worker, clock);

        double t0 = SnnClock.now();
        executor.start();
        SnnTransferservice.transfer(42);
        executor.stop();
        double t1 = SnnClock.now();

        // TODO Problem! Aufruf: now(), now(), beat() statt now(), beat(), now()

        System.out.println("Counter nach Test: " + t0);
        System.out.println("Counter nach Test: " + t1);

        executor.shutdown();
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
