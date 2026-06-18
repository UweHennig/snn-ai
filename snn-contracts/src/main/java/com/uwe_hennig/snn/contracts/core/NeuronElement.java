/**
 * @(#)NeuronElement.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.core;

/**
 * NeuronElement
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public interface NeuronElement {
    NeuronElementType getType();
    void stimulate(int stimulusIdentifier);
}
