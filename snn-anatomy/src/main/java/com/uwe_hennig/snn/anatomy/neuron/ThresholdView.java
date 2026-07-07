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
    private static final float MIN_THRESHOLD = -55f;
    private static final float MAX_THRESHOLD = -50f;

    private final ThresholdModel model;
    private final int            index;

    public ThresholdView(int index, ThresholdModel model) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;

    }

    // ----- Getter/Setter -----

    public ThresholdModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    public float getThreshold() {
        return model.getThreshold(index);
    }

    // ----- Domain Logic -----

    public void applyTimeFeedback(float deltaTimeFeedback) {
        float threshold = model.getThreshold(index);
        threshold += deltaThreshold(deltaTimeFeedback);
        threshold = Math.clamp(threshold, MIN_THRESHOLD, MAX_THRESHOLD);
        model.setThreshold(index, threshold);
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
