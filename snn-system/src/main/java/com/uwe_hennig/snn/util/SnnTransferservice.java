/**
 * @(#)SnnTransferservice.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

/**
 * SnnTransferservice
 * @author Uwe Hennig
 */
public class SnnTransferservice {
    private static SnnTransferservice instance;
    private final TransferWorker transferWorker;

    private SnnTransferservice(TransferWorker worker) {
        this.transferWorker = worker;
    }

    public static SnnTransferservice of(TransferWorker worker) {
        instance = new SnnTransferservice(worker);
        return instance;
    }


    public static void transfer(int stimulusId) {
        if (instance != null) {
            SnnExecutor.get().addTask(stimulusId);
        }
    }

    public static void syncTransfer(int stimulusId) {
        if (instance != null) {
            instance.transferWorker.emitt(stimulusId);
        }
    }
}
