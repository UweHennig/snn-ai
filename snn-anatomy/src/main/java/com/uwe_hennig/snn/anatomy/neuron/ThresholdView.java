/**
 * @(#)ThresholdView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * ThresholdView
 *
 * @author Uwe Hennig
 */
public final class ThresholdView {
    private final float MIN_THRESHOLD = -55f;
    private final float MAX_THRESHOLD = -50f;

    private final ThresholdModel model;
    private final int            index;

    public ThresholdView(int index, ThresholdModel model) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;

    }

    public ThresholdModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    // ----- Domain Logic -----

    public void applyTimeFeedback(float deltaTimeFeedback) {
        model.writeLock(index);
        try {
            float threshold = model.getThreshold(index);
            threshold += deltaThreshold(deltaTimeFeedback);
            threshold = Math.clamp(threshold, MIN_THRESHOLD, MAX_THRESHOLD);
            model.setThreshold(index, threshold);
        } finally {
            model.writeUnlock(index);
        }
    }

    public float getThreshold() {
        return model.getThreshold(index);
    }

    // ----- convenience -----

    float deltaThreshold(float deltaTimeFeedback) {
        float feedbackTimeLimit = model.getTimeLimit(index);
        float phase = Math.clamp(Math.abs(deltaTimeFeedback / feedbackTimeLimit), 0f, 1f);
        float effect = phase * phase * phase;
        float thresholdScale = model.getThresholdScale(index);
        float deltaThreshold = thresholdScale * effect * Math.signum(deltaTimeFeedback);
        return deltaThreshold;
    }
}
