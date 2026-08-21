/**
 * @(#)EventType.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.core;

/**
 * EventType
 *
 * @author Uwe Hennig
 */
public enum EventType {
    DIRECT(0), LIST(1), MATRIX(2);

    private final int code;

    EventType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }
}
