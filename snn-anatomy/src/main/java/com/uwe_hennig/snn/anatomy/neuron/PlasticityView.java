/**
 * @(#)PlasticityView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.PlasticityModelManager;

/**
 * PlasticityView
 *
 * @author Uwe Hennig
 */
public final class PlasticityView {

    // is always called before others
    public static float updatePlasticityPotential(int index, float currentTime) {
        PlasticityModel model = PlasticityModelManager.instance().getModel();
        model.writeLock(index);
        try {
            float lastUpdateTime = model.getLastUpdateTime(index);
            float elapsed = currentTime - lastUpdateTime;
            float currentPot = model.getCurrentPotential(index);
            // 1. Apply to dementia
            float restingPot = model.getRestingPotential(index);
            float restingTime = model.getRestingTime(index);
            float restingRate = model.getRestingRate(index);
            float newPot = update(currentPot, restingPot, restingTime, restingRate, elapsed);
            // 2. Apply to learn
            float targetPot = model.getTargetPotential(index);
            float targetTime = model.getTargetTime(index);
            float learnRate = model.getTargetRate(index);
            newPot = update(newPot, targetPot, targetTime, learnRate, elapsed);
            model.setCurrentPotential(index, newPot);
            model.setLastUpdateTime(index, currentTime);
            return newPot;
        } finally {
            model.writeUnlock(index);
        }
    }

    // is called when value adjustments are made
    public static void applyValueFeedback(int index, float deltaValueFeedback, float currentTime) {
        if (Math.abs(deltaValueFeedback) < 0.001f) {
            return;
        }

        PlasticityModel model = PlasticityModelManager.instance().getModel();
        model.writeLock(index);

        try {
            float elapsed = currentTime - model.getLastUpdateTime(index);
            float targetPot = model.getTargetPotential(index);
            float restingPot = model.getRestingPotential(index);
            float targetTime = model.getTargetTime(index);
            float restingTime = model.getRestingTime(index);
            // targetPot fluctuates between 50mv and 70mV
            // restingPoot fluctuates between -90mv and -40mV
            if (deltaValueFeedback > 0) {
                targetPot = update(targetPot, 90f, targetTime, 3f, elapsed);
                restingPot = update(restingPot, -40f, restingTime, 3f, elapsed);
            } else {
                targetPot = update(targetPot, 50f, targetTime, 3f, elapsed);
                restingPot = update(restingPot, -90f, restingTime, 3f, elapsed);
            }
            model.setTargetPotential(index, targetPot);
            model.setRestingPotential(index, restingPot);
            model.setLastUpdateTime(index, currentTime);
        } finally {
            model.writeUnlock(index);
        }
    }

    public static float getCurrentPotential(int index) {
        PlasticityModel model = PlasticityModelManager.instance().getModel();
        try {
            model.readLock(index);
            return model.getCurrentPotential(index);
        } finally {
            model.readUnlock(index);
        }
    }

    // is called when time adjustments are made
    public static void applyTimeFeedback(int index, float deltaTimeFeedback, float currentTime) {
        if (Math.abs(deltaTimeFeedback) < 0.001f) {
            return;
        }
        PlasticityModel model = PlasticityModelManager.instance().getModel();
        model.writeLock(index);

        try {
            float totalTime = model.getTargetTime(index);
            float elapsed = currentTime - model.getLastUpdateTime(index);
            float tauDominatorTarget = model.getTargetRate(index);
            float tauDominatorResting = model.getRestingRate(index);
            // tauDominators fluctuates between 2 and 5
            if (deltaTimeFeedback < 0.0f) {
                tauDominatorTarget = update(tauDominatorTarget, 2.0f, totalTime, 3f, elapsed);
                tauDominatorResting = update(tauDominatorResting, 5.0f, totalTime, 3f, elapsed);
            } else {
                tauDominatorTarget = update(tauDominatorTarget, 5.0f, totalTime, 3f, elapsed);
                tauDominatorResting = update(tauDominatorResting, 2.0f, totalTime, 3f, elapsed);
            }
            model.setTargetRate(index, tauDominatorTarget);
            model.setRestingRate(index, tauDominatorTarget);
            model.setLastUpdateTime(index, currentTime);
        } finally {
            model.writeUnlock(index);
        }
    }

    /**
     * Alpha function / Euler integration
     */
    private static float update(float currentValue, float targetValue, float maxTimeRange, float tauDominator, float elapsed) {
        float tau = maxTimeRange / tauDominator;
        float alpha = elapsed / (tau + elapsed);

        return currentValue + (targetValue - currentValue) * alpha;
    }

}
