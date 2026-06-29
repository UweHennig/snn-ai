/**
 * @(#)Environment.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.learning.contracts;

import com.uwe_hennig.snn.contracts.peripheral.EnvAction;
import com.uwe_hennig.snn.contracts.peripheral.EnvSignal;
import com.uwe_hennig.snn.contracts.peripheral.EnvironmentPeripheral;

/**
 * Environment
 *
 * @author Uwe Hennig
 */
public interface Environment extends EnvironmentPeripheral {
    // At least one action
    void onAction(EnvAction actionPort, EnvSignal<?> signal);

    void start();
    void stop();
}
