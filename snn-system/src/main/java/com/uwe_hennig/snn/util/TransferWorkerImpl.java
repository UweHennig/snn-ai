/**
 * @(#)TransferWorkerImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

/**
 * TransferWorkerImpl
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class TransferWorkerImpl implements TransferWorker {
    @Override
    public void emitt(int stimulusId) {
        // TODO:
        // 1) Get the stimulus for this ID
        // 2) then the target ID
        // 3) Get an instance of the object with this target ID
        // 4) Call on target objects: stimulate(stimulusId);
        System.out.printf("%nTransfering %d");
    }
}
