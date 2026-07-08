/**
 * @(#)WeightView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.WeightModelManager;

/**
 * WeightView
 *
 * @author Uwe Hennig
 */
public final class WeightView {
    // ----- Domain Logic -----

    // The method is called only by the dendrite corresponding to the timulusType
    public static float applyStimulus(int index, float potential, float currentTime) {
        WeightModel model = WeightModelManager.instance().getModel();
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

    // The method is called only by the dendrite corresponding to the timulusType
    public static int applyFeedback(int index, float deltaTimeFeedback) {
        WeightModel model = WeightModelManager.instance().getModel();
        model.writeLock(index);
        try {
            float preSynapticTime = model.getPreSynapticTime(index);
            float postSynapticTime = model.getPostSynapticTime(index);

            float dt = Math.max(postSynapticTime - preSynapticTime, 0);
            float deltaWeight = deltaWeight(index, deltaTimeFeedback) + deltaHebbWeight(index, deltaTimeFeedback, dt);

            float weight = Math.clamp(deltaWeight + model.getWeight(index), 0f, 1f);
            model.setWeight(index, weight);

            return index;
        } catch (Exception e) {
            return -1;
        } finally {
            model.writeUnlock(index);
        }
    }

    // ----- convenience -----

    static float deltaWeight(int index, float deltaTimeFeedback) {
        WeightModel model = WeightModelManager.instance().getModel();
        float timeLimit = model.getTimeLimit(index);
        float phase = Math.clamp(Math.abs(deltaTimeFeedback / timeLimit), 0f, 1f);
        float effect = 1 - phase * phase * phase;
        float weightScale = model.getWeightScale(index);

        return weightScale * effect * Math.signum(deltaTimeFeedback);
    }

    static float deltaHebbWeight(int index, float deltaTimeFeedback, float dt) {
        WeightModel model = WeightModelManager.instance().getModel();
        float hebbTimeRange = model.getHebbTimeRange(index);
        if (dt >= hebbTimeRange || hebbTimeRange == 0) {
            return 0.0f;
        }
        float proximity = 1f - (dt / hebbTimeRange);
        float hebbScale = model.getHebbScale(index);

        return proximity * hebbScale * Math.signum(deltaTimeFeedback);
    }

    public static void initDefaultValues(int index) {
        WeightModel model = WeightModelManager.instance().getModel();
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
