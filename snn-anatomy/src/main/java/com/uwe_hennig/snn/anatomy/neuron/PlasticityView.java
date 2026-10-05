/// @(#)PlasticityView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// PlasticityView
///
/// @author Uwe Hennig
///
public final class PlasticityView {
    private PlasticityView() {
    }

    // ----- Domain Logic -----

    // is always called before others
    public static float updatePlasticityPotential(PlasticityModel model, int offset, float currentTime) {
        float lastUpdateTime = model.getLastUpdateTime(offset);
        float elapsed = currentTime - lastUpdateTime;
        float currentPot = model.getCurrentPotential(offset);

        // 1. Apply to dementia
        float restingPot = model.getRestingPotential(offset);
        float restingTime = model.getRestingTime(offset);
        float restingRate = model.getRestingRate(offset);
        float newPot = update(currentPot, restingPot, restingTime, restingRate, elapsed);

        // 2. Apply to learn
        float targetPot = model.getTargetPotential(offset);
        float targetTime = model.getTargetTime(offset);
        float learnRate = model.getTargetRate(offset);
        newPot = update(newPot, targetPot, targetTime, learnRate, elapsed);
        model.setCurrentPotential(offset, newPot);
        model.setLastUpdateTime(offset, currentTime);
        return newPot;
    }

    // is called when value adjustments are made
    public static void applyValueFeedback(PlasticityModel model, int offset, float deltaValueFeedback, float currentTime) {
        if (Math.abs(deltaValueFeedback) < 0.001f) {
            return;
        }

        float elapsed = currentTime - model.getLastUpdateTime(offset);
        float targetPot = model.getTargetPotential(offset);
        float restingPot = model.getRestingPotential(offset);
        float targetTime = model.getTargetTime(offset);
        float restingTime = model.getRestingTime(offset);
        // targetPot fluctuates between 50mv and 70mV
        // restingPoot fluctuates between -90mv and -40mV
        if (deltaValueFeedback > 0) {
            targetPot = update(targetPot, 90f, targetTime, 3f, elapsed);
            restingPot = update(restingPot, -40f, restingTime, 3f, elapsed);
        } else {
            targetPot = update(targetPot, 50f, targetTime, 3f, elapsed);
            restingPot = update(restingPot, -90f, restingTime, 3f, elapsed);
        }
        model.setTargetPotential(offset, targetPot);
        model.setRestingPotential(offset, restingPot);
        model.setLastUpdateTime(offset, currentTime);
    }

    public static float getCurrentPotential(PlasticityModel model, int offset) {
        return model.getCurrentPotential(offset);
    }

    // is called when time adjustments are made
    public static void applyTimeFeedback(PlasticityModel model, int offset, float deltaTimeFeedback, float currentTime) {
        if (Math.abs(deltaTimeFeedback) < 0.001f) {
            return;
        }
        float totalTime = model.getTargetTime(offset);
        float elapsed = currentTime - model.getLastUpdateTime(offset);
        float tauDominatorTarget = model.getTargetRate(offset);
        float tauDominatorResting = model.getRestingRate(offset);
        // tauDominators fluctuates between 2 and 5
        if (deltaTimeFeedback < 0.0f) {
            tauDominatorTarget = update(tauDominatorTarget, 2.0f, totalTime, 3f, elapsed);
            tauDominatorResting = update(tauDominatorResting, 5.0f, totalTime, 3f, elapsed);
        } else {
            tauDominatorTarget = update(tauDominatorTarget, 5.0f, totalTime, 3f, elapsed);
            tauDominatorResting = update(tauDominatorResting, 2.0f, totalTime, 3f, elapsed);
        }
        model.setTargetRate(offset, tauDominatorTarget);
        model.setRestingRate(offset, tauDominatorTarget);
        model.setLastUpdateTime(offset, currentTime);
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
