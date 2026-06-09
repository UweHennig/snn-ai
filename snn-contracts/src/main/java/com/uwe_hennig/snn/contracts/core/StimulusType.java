/**
 * @(#)StimulusType.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.core;

/**
 * StimulusType
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public enum StimulusType {
    STIMULUS(0x01), TIME_FEEDBACK(0x02), VALUE_FEEDBACK(0x04);

    private final int code;

    StimulusType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
