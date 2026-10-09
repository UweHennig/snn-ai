/// @(#)ThresholdView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// ThresholdView
///
/// @author Uwe Hennig
///
public final class ThresholdView {
    private static final float MIN_THRESHOLD = -55f;
    private static final float MAX_THRESHOLD = -50f;

    private ThresholdView() {
    }

    // ----- Domain Logic -----

    public static void applyTimeFeedback(ThresholdModel model, int offset, float deltaTimeFeedback) {
        float threshold = model.getThreshold(offset);
        threshold += deltaThreshold(model, offset, deltaTimeFeedback);
        threshold = Math.clamp(threshold, MIN_THRESHOLD, MAX_THRESHOLD);
        model.setThreshold(offset, threshold);
    }

    public static float getThreshold(ThresholdModel model, int offset) {
        return model.getThreshold(offset);
    }

    public static int claim(ThresholdModel model) {
        return model.claimSlot();
    }

    // ----- convenience -----

    static float deltaThreshold(ThresholdModel model, int offset, float deltaTimeFeedback) {
        float feedbackTimeLimit = model.getTimeLimit(offset);
        float phase = Math.clamp(Math.abs(deltaTimeFeedback / feedbackTimeLimit), 0f, 1f);
        float effect = phase * phase * phase;
        float thresholdScale = model.getThresholdScale(offset);
        float deltaThreshold = thresholdScale * effect * Math.signum(deltaTimeFeedback);
        return deltaThreshold;
    }
}
