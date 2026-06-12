/**
 * @(#)WeightView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * WeightView
 *
 * @author Uwe Hennig
 */
public final class WeightView {
    private final WeightModel model;
    private final int         index;

    public WeightView(int index, WeightModel model) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;

        initDefaultValues();
    }

    public WeightModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    // ----- Domain Logic -----

    // The method is called only by the dendrite corresponding to the timulusType
    public float applyStimulus(float potential, float currentTime) {
        model.lock(index);
        try {
            float preSynapticTime = model.getPostSynapticTime(index);

            // swap time
            model.setPreSynapticTime(index, preSynapticTime);
            model.setPostSynapticTime(index, currentTime);

            float newPotential = model.getWeight(index) * potential;

            return newPotential;
        } finally {
            model.unlock(index);
        }
    }

    // The method is called only by the dendrite corresponding to the timulusType
    public int applyFeedback(float deltaTimeFeedback) {
        model.lock(index);
        try {
            float preSynapticTime = model.getPreSynapticTime(index);
            float postSynapticTime = model.getPostSynapticTime(index);

            float dt = Math.max(postSynapticTime - preSynapticTime, 0);
            float deltaWeight = deltaWeight(deltaTimeFeedback) + deltaHebbWeight(deltaTimeFeedback, dt);

            float weight = Math.clamp(deltaWeight + model.getWeight(index), 0f, 1f);
            model.setWeight(index, weight);

            return index;
        } catch (Exception e) {
            return -1;
        } finally {
            model.unlock(index);
        }
    }

    // ----- convenience -----

    float deltaWeight(float deltaTimeFeedback) {
        float timeLimit = model.getTimeLimit(index);
        float phase = Math.clamp(Math.abs(deltaTimeFeedback / timeLimit), 0f, 1f);
        float effect = 1 - phase * phase * phase;
        float weightScale = model.getWeightScale(index);

        return weightScale * effect * Math.signum(deltaTimeFeedback);
    }

    float deltaHebbWeight(float deltaTimeFeedback, float dt) {
        float hebbTimeRange = model.getHebbTimeRange(index);
        if (dt >= hebbTimeRange || hebbTimeRange == 0) {
            return 0.0f;
        }
        float proximity = 1f - (dt / hebbTimeRange);
        float hebbScale = model.getHebbScale(index);

        return proximity * hebbScale * Math.signum(deltaTimeFeedback);
    }

    void initDefaultValues() {
        if (model.getWeightScale(index) == 0.0f) {
            model.setWeightScale(index, 0.01f);
        }
        if (model.getHebbScale(index) == 0.0f) {
            model.setHebbScale(index, 0.005f);
        }
        if (model.getTimeLimit(index) == 0.0f) {
            model.setTimeLimit(index, 200f);
        }
        if (model.getHebbTimeRange(index) == 0.0f) {
            model.setHebbTimeRange(index, 1f);
        }
    }
}
