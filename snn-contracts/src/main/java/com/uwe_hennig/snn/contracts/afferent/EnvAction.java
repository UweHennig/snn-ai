/**
 * @(#)EnvAction.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.afferent;

/**
 * EnvAction
 *
 * @author Uwe Hennig
 */
public interface EnvAction<A> {
    long getIdentifier();
    void invoke(EnvSignal<?> signal);
}
