/**
 * @(#)EffectorConsumer.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * EffectorConsumer
 *
 * @author Uwe Hennig
 */
@FunctionalInterface
public interface EffectorConsumer {
    void accept(SnnEffector effector, float value);
}
