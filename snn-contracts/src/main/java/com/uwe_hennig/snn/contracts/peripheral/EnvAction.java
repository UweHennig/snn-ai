/**
 * @(#)EnvAction.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * EnvAction
 *
 * @author Uwe Hennig
 */
public interface EnvAction {
    long getIdentifier();

    void withConsumer(ActionConsumer consumer);
    void invoke(EnvSignal<?> signal);
}
