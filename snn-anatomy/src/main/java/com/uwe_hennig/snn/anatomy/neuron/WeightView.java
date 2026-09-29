/**
 * @(#)WeightView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/// WeightView
///
/// @author Uwe Hennig
public final class WeightView {
    private WeightView() {
    }

    // ----- Domain Logic -----

    // The method is called only by the dendrite corresponding to the stimulus type
    public static float applyStimulus(WeightModel model, int offset, float potential, float currentTime) {
        try {
            float preSynapticTime = model.getPostSynapticTime(offset);

            // swap time
            model.setPreSynapticTime(offset, preSynapticTime);
            model.setPostSynapticTime(offset, currentTime);

            float newPotential = model.getWeight(offset) * potential;

            return newPotential;
        } finally {
            model.writeUnlock(offset);
        }
    }

    // The method is called only by the dendrite corresponding to the stimulus type
    public static int applyFeedback(WeightModel model, int offset, float deltaTimeFeedback) {
        model.writeLock(offset);
        try {
            float preSynapticTime = model.getPreSynapticTime(offset);
            float postSynapticTime = model.getPostSynapticTime(offset);

            float dt = Math.max(postSynapticTime - preSynapticTime, 0);
            float deltaWeight = deltaWeight(model, offset, deltaTimeFeedback) + deltaHebbWeight(model, offset, deltaTimeFeedback, dt);

            float weight = Math.clamp(deltaWeight + model.getWeight(offset), 0f, 1f);
            model.setWeight(offset, weight);

            return offset;
        } catch (Exception e) {
            return -1;
        } finally {
            model.writeUnlock(offset);
        }
    }

    public float getWeight(WeightModel model, int offset) {
        try {
            model.readLock(offset);
            return model.getWeight(offset);
        } finally {
            model.readUnlock(offset);
        }
    }

    // ----- convenience -----

    static float deltaWeight(WeightModel model, int offset, float deltaTimeFeedback) {
        float timeLimit = model.getTimeLimit(offset);
        float phase = Math.clamp(Math.abs(deltaTimeFeedback / timeLimit), 0f, 1f);
        float effect = 1 - phase * phase * phase;
        float weightScale = model.getWeightScale(offset);

        return weightScale * effect * Math.signum(deltaTimeFeedback);
    }

    static float deltaHebbWeight(WeightModel model, int offset, float deltaTimeFeedback, float dt) {
        float hebbTimeRange = model.getHebbTimeRange(offset);
        if (dt >= hebbTimeRange || hebbTimeRange == 0) {
            return 0.0f;
        }
        float proximity = 1f - (dt / hebbTimeRange);
        float hebbScale = model.getHebbScale(offset);

        return proximity * hebbScale * Math.signum(deltaTimeFeedback);
    }

    public void initDefaultValues(WeightModel model, int offset) {
        model.writeLock(offset);
        try {
            if (model.getWeightScale(offset) == 0.0f) {
                model.setWeightScale(offset, 0.01f);
            }
            if (model.getHebbScale(offset) == 0.0f) {
                model.setHebbScale(offset, 0.005f);
            }
            if (model.getTimeLimit(offset) == 0.0f) {
                model.setTimeLimit(offset, 200f);
            }
            if (model.getHebbTimeRange(offset) == 0.0f) {
                model.setHebbTimeRange(offset, 1f);
            }
            model.setWeight(offset, 0.50f);
        } finally {
            model.writeUnlock(offset);
        }
    }
}
