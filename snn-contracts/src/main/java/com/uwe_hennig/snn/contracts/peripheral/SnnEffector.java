/**
 * @(#)Effector.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * SnnEffector
 *
 * @author Uwe Hennig
 */
public interface SnnEffector {
    void withConsumer(EffectorConsumer consumer);
    long getIdentifier();
    float getValue(int row, int column);
}
