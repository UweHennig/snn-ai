/**
 * @(#)Effector.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.afferent;

/**
 * SnnEffector
 *
 * @author Uwe Hennig
 */
public interface SnnEffector {
    void setConsumer(EffectorConsumer consumer);
    long getIdentifier();
}
