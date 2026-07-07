/**
 * @(#)ThresholdView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.ThresholdModelManager;

/**
 * ThresholdView
 *
 * @author Uwe Hennig
 */
public final class ThresholdView {
    private static final float MIN_THRESHOLD = -55f;
    private static final float MAX_THRESHOLD = -50f;

    // ----- Domain Logic -----

    public static void applyTimeFeedback(int index, float deltaTimeFeedback) {
        ThresholdModel model = ThresholdModelManager.instance().getModel();

        try {
            model.writeLock(index);
            float threshold = model.getThreshold(index);
            threshold += deltaThreshold(index, deltaTimeFeedback);
            threshold = Math.clamp(threshold, MIN_THRESHOLD, MAX_THRESHOLD);
            model.setThreshold(index, threshold);
        } finally {
            model.writeUnlock(index);
        }
    }

    public static float getThreshold(int index) {
        ThresholdModel model = ThresholdModelManager.instance().getModel();
        try {
            model.readLock(index);
            return model.getThreshold(index);
        } finally {
            model.readUnlock(index);
        }
    }

    // ----- convenience -----

    static float deltaThreshold(int index, float deltaTimeFeedback) {
        ThresholdModel model = ThresholdModelManager.instance().getModel();

        float feedbackTimeLimit = model.getTimeLimit(index);
        float phase = Math.clamp(Math.abs(deltaTimeFeedback / feedbackTimeLimit), 0f, 1f);
        float effect = phase * phase * phase;
        float thresholdScale = model.getThresholdScale(index);
        float deltaThreshold = thresholdScale * effect * Math.signum(deltaTimeFeedback);
        return deltaThreshold;
    }
}
