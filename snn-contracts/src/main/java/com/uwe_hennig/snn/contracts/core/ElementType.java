/**
 * @(#)ElementType.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.core;

/**
 * ElementType
 *
 * @author Uwe Hennig
 */
public final class ElementType {
    public final static int DENDRIT  = 1 << 0;
    public final static int SOMA     = 1 << 1;
    public final static int AXON     = 1 << 2;
    public final static int SYNAPSE  = 1 << 3;

    public final static int RECEPTOR = 1 << 4;
    public final static int EFFECTOR = 1 << 5;

    public static boolean isNeruonElement(int value) {
        return !isReceptor(value) && !isEffector(value);
    }

    public static boolean isReceptor(int value) {
        return (value & RECEPTOR) > 0;
    }

    public static boolean isEffector(int value) {
        return (value & EFFECTOR) > 0;
    }

    public static boolean isDendrit(int value) {
        return (value & DENDRIT) > 0;

    }

    public static boolean isSoma(int value) {
        return (value & SOMA) > 0;
    }

    public static boolean isAxon(int value) {
        return (value & AXON) > 0;
    }

    public static boolean isSynapse(int value) {
        return (value & SYNAPSE) > 0;
    }

}
