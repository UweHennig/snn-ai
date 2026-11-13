/// @(#)NeuronFieldType.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.contracts.core;

/// NeuronFieldType
///
/// @author Uwe Hennig
///
public enum NeuronFieldType {
    INVALID_FIELD(Code.INVALID),
    RECEPTOR_FIELD(Code.RECEPTOR),
    ASSOCIATIVE_FIELD(Code.ASSOCIATIVE),
    EFFECTOR_FIELD(Code.EFFECTOR),
    FEEDBACK_FIELD(Code.FEEDBACK);

    public static final class Code {
        public static final int INVALID     = 0;
        public static final int RECEPTOR    = 1 << 0;
        public static final int ASSOCIATIVE = 1 << 1;
        public static final int EFFECTOR    = 1 << 2;
        public static final int FEEDBACK    = 1 << 3;
        private Code() {}
    }

    private final int code;

    NeuronFieldType(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public boolean isInMask(int mask) {
        return (mask & code) != 0;
    }

    public static NeuronFieldType fromCode(int code) {
        return switch (code) {
            case Code.RECEPTOR    -> RECEPTOR_FIELD;
            case Code.ASSOCIATIVE -> ASSOCIATIVE_FIELD;
            case Code.EFFECTOR    -> EFFECTOR_FIELD;
            case Code.FEEDBACK    -> FEEDBACK_FIELD;
            default               -> INVALID_FIELD;
        };
    }
}