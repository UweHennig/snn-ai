/**
 * @(#)EnvironmentPeripheral.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

import java.util.List;

/**
 * EnvironmentPeripheral
 *
 * @author Uwe Hennig
 */
public interface EnvironmentPeripheral {
    List<EnvAction> getEnvActions();
    List<EnvState> getEnvState();
    List<EnvFeedback> getEnvFeedback();
}
