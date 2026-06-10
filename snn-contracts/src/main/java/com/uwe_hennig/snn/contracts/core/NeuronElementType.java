/**
 * @(#)NeuronElementType.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.core;

/**
 * NeuronElementType
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public enum NeuronElementType {
    DENDRIT(0x01), SOMA(0x02), AXON(0x04), SYNAPSE(0x05);

    private final int code;

    NeuronElementType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
