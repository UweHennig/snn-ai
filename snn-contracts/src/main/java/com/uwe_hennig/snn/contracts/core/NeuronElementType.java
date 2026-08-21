/**
 * @(#)NeuronElementType.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.core;

/**
 * NeuronElementType
 *
 * @author Uwe Hennig
 */
public enum NeuronElementType {
    DENDRIT(0), SOMA(1), AXON(2), SYNAPSE(3), RECEPTOR(4), EFFECTOR(5);

    private static final int DENDRID_CODE  = 0;
    private static final int SOMA_CODE     = 1;
    private static final int AXON_CODE     = 2;
    private static final int SYNAPSE_CODE  = 3;
    private static final int RECPTOR_CODE  = 4;
    private static final int EFFECTOR_CODE = 5;

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
            case RECPTOR_CODE -> RECEPTOR;
            case EFFECTOR_CODE -> EFFECTOR;
            default -> null;
        };
    }
}
