/**
 * @(#)SnnClock.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.time;

/**
 * SnnClock
 * @author Uwe Hennig
 */
public final class SnnClock {
    private SnnClock() {}

    public static double now() {
        return SnnClockImpl.get().now();
    }
}
