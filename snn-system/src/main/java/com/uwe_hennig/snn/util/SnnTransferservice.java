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

    public static void transfer(int stimulusId, int type) {
        SnnDispatcher.getInstance().offer(stimulusId << 2 + (type & 0x03));
    }
}
