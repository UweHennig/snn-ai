/**
 * @(#)CleanupCallback.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.analytics.ui;

/**
 * CleanupCallback
 *
 * @author Uwe Hennig
 */
@FunctionalInterface
public interface CleanupCallback {
    void onCleanup();
}
