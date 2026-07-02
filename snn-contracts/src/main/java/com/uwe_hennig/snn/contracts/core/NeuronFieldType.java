/**
 * @(#)NeuronFieldType.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.core;

/**
 * NeuronFieldType
 *
 * @author Uwe Hennig
 */
public enum NeuronFieldType {
    AFFERENT(1), ASSOCIATIVE(2), EFFERENT(3), FEEDBACK(4), UNDEFINED(5);

    private final int code;

    NeuronFieldType(int code) {
        this.code = code;
    }

    public int code() {
        return this.code;
    }

    public static NeuronFieldType fieldType(int code) {
        return switch (code) {
            case 1 -> AFFERENT;
            case 2 -> ASSOCIATIVE;
            case 3 -> EFFERENT;
            case 4 -> FEEDBACK;
            default -> UNDEFINED;
        };
    }
}
