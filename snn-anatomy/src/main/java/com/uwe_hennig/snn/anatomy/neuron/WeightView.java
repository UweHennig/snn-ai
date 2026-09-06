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
    private final int index;

    public WeightView(WeightModel model, int index) {
        this.model = model;
        this.index = index;
    }

    // ----- Domain Logic -----

    // The method is called only by the dendrite corresponding to the stimulus type
    public float applyStimulus(float potential, float currentTime) {
        try {
            float preSynapticTime = model.getPostSynapticTime(index);

            // swap time
            model.setPreSynapticTime(index, preSynapticTime);
            model.setPostSynapticTime(index, currentTime);

            float newPotential = model.getWeight(index) * potential;

            return newPotential;
        } finally {
            model.writeUnlock(index);
        }
    }

    // The method is called only by the dendrite corresponding to the stimulus type
    public int applyFeedback(float deltaTimeFeedback) {
        model.writeLock(index);
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
            model.writeUnlock(index);
        }
    }

    public float getWeight() {
        try {
            model.readLock(index);
            return model.getWeight(index);
        } finally {
            model.readUnlock(index);
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

    public void initDefaultValues() {
        model.writeLock(index);
        try {
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
            model.setWeight(index, 0.50f);
        } finally {
            model.writeUnlock(index);
        }
    }
}
