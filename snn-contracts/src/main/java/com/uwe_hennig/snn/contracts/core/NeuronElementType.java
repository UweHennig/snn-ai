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
    DENDRIT(0x00), SOMA(0x01), AXON(0x02), SYNAPSE(0x03);

    private static final int DENDRID_CODE = 0x00;
    private static final int SOMA_CODE    = 0x01;
    private static final int AXON_CODE    = 0x02;
    private static final int SYNAPSE_CODE = 0x03;

    private final int code;

    NeuronElementType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static NeuronElementType of(int code) {
        return switch (code) {
            case DENDRID_CODE -> DENDRIT;
            case SOMA_CODE -> SOMA;
            case AXON_CODE -> AXON;
            case SYNAPSE_CODE -> SYNAPSE;
            default -> null;
        };
    }
}
