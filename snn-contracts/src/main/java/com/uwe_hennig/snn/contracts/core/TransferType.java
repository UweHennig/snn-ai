/**
 * @(#)TransferType.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.core;

/**
 * TransferType
 *
 * @author Uwe Hennig
 */
public enum TransferType {
    DIRECT(0), LIST(1), MATRIX(2);

    private final int code;

    TransferType(int code) {
        this.code = code;
    }

    public static TransferType fromCode(int value) {
        return switch(value) {
            case 0 -> DIRECT;
            case 1 -> LIST;
            case 2 -> MATRIX;
            default -> null;
        };
    }

    public int code() {
        return code;
    }
}
