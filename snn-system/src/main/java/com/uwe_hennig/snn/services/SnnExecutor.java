/**
 * @(#)SnnExecutor.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.services;

import com.uwe_hennig.snn.time.SnnClock;

/**
 * SnnExecutor
 * @author Uwe Hennig
 */
public class SnnExecutor {
    public static void submit(Runnable task) {
        SnnClock.instance().submit(task);
    }
}
