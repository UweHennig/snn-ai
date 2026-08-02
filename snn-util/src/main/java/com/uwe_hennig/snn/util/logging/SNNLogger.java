/**
 * @(#)SNNLogger.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util.logging;

import java.util.function.Supplier;

/**
 * SNNLogger
 *
 * @author Uwe Hennig
 */
public class SNNLogger {
    private boolean active = Boolean.getBoolean("snn.logging");

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isActive() {
        return active;
    }

    public void debug(Supplier<String> messageSupplier) {
        if (active) {
            System.out.println(messageSupplier.get());
        }
    }

    public void error(Supplier<String> messageSupplier) {
        System.err.println(messageSupplier.get());
    }
}
