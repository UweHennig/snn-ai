/**
 * @(#)SnnReceptor.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

import com.uwe_hennig.snn.contracts.core.StimulusType;

/**
 * SnnReceptor
 *
 * @author Uwe Hennig
 */
public interface SnnReceptor {
    void perceive(StimulusType stimulusType, float [][] value);
}
