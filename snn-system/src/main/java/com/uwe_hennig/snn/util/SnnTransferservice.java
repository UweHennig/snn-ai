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
public final class SnnTransferservice {
    private SnnTransferservice() {}

    public static void transfer(int stimulusId) {
        SnnDispatcher.getInstance().offer(stimulusId);
    }
}
