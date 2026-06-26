/**
 * @(#)SnnFeedback.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.afferent;

import com.uwe_hennig.snn.contracts.core.StimulusType;

/**
 * SnnFeedback
 *
 * @author Uwe Hennig
 */
public interface SnnFeedback {
    void perceive(float value);
    StimulusType getType();
}
