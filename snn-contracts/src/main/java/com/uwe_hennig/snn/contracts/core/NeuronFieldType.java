/// @(#)NeuronFieldType.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.contracts.core;

/// NeuronFieldType
///
/// @author Uwe Hennig
///
public final class NeuronFieldType {
    public static final int RECEPTOR_FIELD    = 1 << 0;
    public static final int ASSOCIATIVE_FIELD = 1 << 1;
    public static final int EFFECTOR_FIELD    = 1 << 2;
    public static final int FEEDBACK_FIELD    = 1 << 3;

    public static boolean isReceptorField(int code) {
        return (code & RECEPTOR_FIELD) > 0;
    }

    public static boolean isAssociativeField(int code) {
        return (code & ASSOCIATIVE_FIELD) > 0;
    }

    public static boolean isEffectorField(int code) {
        return (code & EFFECTOR_FIELD) > 0;
    }

    public static boolean isFeedbackField(int code) {
        return (code & FEEDBACK_FIELD) > 0;
    }

    public static String toString(int type) {
        return switch(type) {
            case RECEPTOR_FIELD -> "RECEPTOR_FIELD";
            case ASSOCIATIVE_FIELD -> "ASSOCIATIVE_FIELD";
            case EFFECTOR_FIELD -> "EFFECTOR_FIELD";
            case FEEDBACK_FIELD -> "FEEDBACK_FIELD";
            default -> "INVALID_FIELD_TYPE";
        };
    }
}
