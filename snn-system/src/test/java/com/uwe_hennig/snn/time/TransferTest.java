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

/**
 * TransferBeatTest
 *
 * @author Uwe Hennig
 */
public class TransferTest {

    @Test
    @DisplayName("Simple time Test")
    public void testTime() throws Exception {
        TransferWorker worker = new TestTransferWorker();
        SnnExecutor executor = SnnExecutor.of(1024L, 4, worker);

        executor.start();
        System.out.println("Starting transfer...");
        for (int i = 0; i < 100; i++) {
            SnnTransferservice.transfer(i);
        }
        executor.stop();
        executor.shutdown();
    }

    public final class TestTransferWorker implements TransferWorker {
        @Override
        public void emitt(int stimulusId) {
            System.out.println("ID: " + stimulusId);
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
