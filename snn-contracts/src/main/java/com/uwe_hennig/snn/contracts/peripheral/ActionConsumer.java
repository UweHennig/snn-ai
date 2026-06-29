/**
 * @(#)ActionConsumer.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * ActionConsumer
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public interface ActionConsumer {
    void accept(EnvAction action, EnvSignal<?> signal);
}
