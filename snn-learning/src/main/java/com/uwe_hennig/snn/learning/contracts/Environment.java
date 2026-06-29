/**
 * @(#)Environment.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.learning.contracts;

import java.util.List;

import com.uwe_hennig.snn.contracts.peripheral.EnvAction;
import com.uwe_hennig.snn.contracts.peripheral.EnvFeedback;
import com.uwe_hennig.snn.contracts.peripheral.EnvSignal;
import com.uwe_hennig.snn.contracts.peripheral.EnvState;

/**
 * Environment
 *
 * @author Uwe Hennig
 */
public interface Environment {
    List<EnvAction> getEnvActionsPorts();
    List<EnvState> getEnvStatePorts();
    List<EnvFeedback> getEnvFeedbackPorts();

    // At least one action
    void onAction(EnvAction actionPort, EnvSignal<?> signal);

    void start();
    void stop();
}
