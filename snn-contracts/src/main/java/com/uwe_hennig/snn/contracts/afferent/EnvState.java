/**
 * @(#)EnvState.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.afferent;

/**
 * EnvState
 *
 * @author Uwe Hennig
 */
public interface EnvState<S> {
    long getIdentifier();
    S getData();

    void withConsumer(StateConsumer consumer);
    void invoke(EnvSignal<?> signal);
}
