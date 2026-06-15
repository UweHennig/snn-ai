/**
 * @(#)SnnTransferservice.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import com.uwe_hennig.snn.time.BalanceWheel;
import com.uwe_hennig.snn.time.SnnClockImpl;

/**
 * SnnTransferservice
 * @author Uwe Hennig
 */
public class SnnTransferservice {
    private static SnnTransferservice instance;

    private final BalanceWheel balanceWheel;
    private final TransferWorker transferWorker;

    private SnnTransferservice(TransferWorker worker, SnnClockImpl clock) {
        this.balanceWheel = clock;
        this.transferWorker = worker;
    }

    public static SnnTransferservice of(TransferWorker worker, SnnClockImpl clock) {
        instance = new SnnTransferservice(worker, clock);
        return instance;
    }


    public static void transfer(int stimulusId) {
        if (instance != null) {
            instance.balanceWheel.beat();
            SnnExecutor.get().addTask(stimulusId);
        }
    }

    public static void syncTransfer(int stimulusId) {
        if (instance != null) {
            instance.balanceWheel.beat();
            instance.transferWorker.emitt(stimulusId);
        }
    }
}
