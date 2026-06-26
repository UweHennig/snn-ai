/**
 * @(#)StateConsumer.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.afferent;

/**
 * StateConsumer
 *
 * @author Uwe Hennig
 */
@FunctionalInterface
public interface StateConsumer {
    void accept(EnvState<?> state, Signal<?> payload);
}
