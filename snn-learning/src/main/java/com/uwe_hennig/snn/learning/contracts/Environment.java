/**
 * @(#)Environment.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.learning.contracts;

import java.time.Duration;
import java.util.List;

import com.uwe_hennig.snn.contracts.afferent.EnvAction;
import com.uwe_hennig.snn.contracts.afferent.EnvFeedback;
import com.uwe_hennig.snn.contracts.afferent.EnvSignal;
import com.uwe_hennig.snn.contracts.afferent.EnvState;

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
