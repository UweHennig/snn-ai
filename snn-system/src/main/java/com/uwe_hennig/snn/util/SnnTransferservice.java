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


    public static boolean transfer(int stimulusId) {
        if (instance != null) {
            return SnnExecutor.get().offer(stimulusId);
        } else {
            throw new RuntimeException("SnnTransferservice instance is null!");
        }
    }

    public static void syncTransfer(int stimulusId) {
        if (instance != null) {
            instance.transferWorker.emitt(stimulusId);
        } else {
            throw new RuntimeException("SnnTransferservice instance is null!");
        }
    }
}
