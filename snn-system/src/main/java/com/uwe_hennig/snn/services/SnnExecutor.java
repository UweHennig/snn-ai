/**
 * @(#)SnnExecutor.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.services;

import com.uwe_hennig.snn.time.SnnClock;

/**
 * SnnExecutor
 * SnnExceutor formally separates the clock incrementing from the Runnable task
 * @author Uwe Hennig
 */
public class SnnExecutor {
    public static void submit(Runnable task) {
        SnnClock.instance().submit(task);
    }
}
