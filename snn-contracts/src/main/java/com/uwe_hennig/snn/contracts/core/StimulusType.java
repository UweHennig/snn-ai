/// @(#)StimulusType.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.contracts.core;

/// StimulusType
///
/// @author Uwe Hennig
public final class StimulusType {
    public static final int STIMULUS       = 1 << 0;
    public static final int TIME_FEEDBACK  = 1 << 1;
    public static final int VALUE_FEEDBACK = 1 << 2;

    public static boolean isStimulus(int value) {
        return (value & STIMULUS) > 0;
    }

    public static boolean isTimeFeedback(int value) {
        return (value & TIME_FEEDBACK) > 0;
    }

    public static boolean isValueFeedback(int value) {
        return (value & VALUE_FEEDBACK) > 0;
    }
}
