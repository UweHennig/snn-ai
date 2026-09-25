/**
 * @(#)SNNLogger.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util.logging;

import java.io.PrintStream;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class SNNLogger {
    private static volatile boolean active = Boolean.getBoolean("snn.logging");
    private static volatile Consumer<String> testCallback = null;
    private static volatile PrintStream out = System.out;
    private static volatile PrintStream err = System.err;

    private SNNLogger() {}

    public static void setPrintStreamOut(PrintStream outStream) {
        out = outStream;
    }

    public static void setPrintStreamErr(PrintStream errStream) {
        err = errStream;
    }

    public static void setActive(boolean value) {
        active = value;
    }

    public static boolean isActive() {
        return active;
    }

    public static void setCallback(Consumer<String> callback) {
        testCallback = callback;
    }

    public static void debug(Supplier<String> messageSupplier) {
        if (active) {
            String msg = messageSupplier.get();
            out.println(msg);

            Consumer<String> cb = testCallback;
            if (cb != null) {
                cb.accept(msg);
            }
        }
    }

    public static void error(Supplier<String> messageSupplier) {
        String msg = messageSupplier.get();
        err.println(msg);

        Consumer<String> cb = testCallback;
        if (cb != null) {
            cb.accept(msg);
        }
    }
}