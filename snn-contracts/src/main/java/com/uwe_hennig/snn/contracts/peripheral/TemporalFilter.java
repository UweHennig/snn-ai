/**
 * @(#)TemporalFilter.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * TemporalFilter
 * The TemporalFilter discards signals that arrive at the receptor within a too short temporal interval
 * following the previous signal. This prevents receptor overload and ensures that only temporally
 * permissible events are further processed.
 * @author Uwe Hennig
 */
public interface TemporalFilter {
    boolean allow(long now, long lastEmit);
}
