/**
 * @(#)PlasticityView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * PlasticityView
 *
 * @author Uwe Hennig
 */
public class PlasticityView {
    private final PlasticityModel model;
    private final long            index;

    public PlasticityView(int index, PlasticityModel model) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
    }

    public PlasticityModel getModel() {
        return model;
    }

    public long getViewId() {
        return index;
    }

    // is always called at the beginning
    public float updatePlasticityPotential(float currentTime) {
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

        try {
            model.lock(index);
            model.setCurrentPotential(index, newPot);
            model.setLastUpdateTime(index, currentTime);
        } finally {
            model.unlock(index);
        }

        return newPot;
    }

    // is called when value adjustments are made
    public void applyValueFeedback(float deltaValueFeedback, float currentTime) {
        if (Math.abs(deltaValueFeedback) < 0.001f) {
            return;
        }
        float elapsed = currentTime - model.getLastUpdateTime(index);

        float targetPot = model.getTargetPotential(index);
        float restingPot = model.getRestingPotential(index);
        float targetTime = model.getTargetTime(index);
        float restingTime = model.getRestingTime(index);

        // targetPot fluctuates between 50mv and 70mV
        // restingPoot fluctuates between -90mv and -50mV
        if (deltaValueFeedback > 0) {
            targetPot  = update(targetPot, 90f, targetTime, 3f, elapsed);
            restingPot = update(restingPot, -50f, restingTime, 3f, elapsed);
        } else {
            targetPot = update(targetPot, 50f, targetTime, 3f, elapsed);
            restingPot = update(restingPot, -90f, restingTime, 3f, elapsed);
        }

        try {
            model.lock(index);
            model.setTargetPotential(index, targetPot);
            model.setRestingPotential(index, restingPot);
            model.setLastUpdateTime(index, currentTime);
        } finally {
            model.unlock(index);
        }
    }

    // is called when a time adjustments are made
    public void applyTimeFeedback(float deltaTimeFeedback, float currentTime) {
        if (Math.abs(deltaTimeFeedback) < 0.001f) {
            return;
        }

        float totalTime = model.getTargetTime(index);
        float elapsed = currentTime - model.getLastUpdateTime(index);
        float tauDominatorTarget  = model.getTargetTime(index);
        float tauDominatorResting = model.getRestingRate(index);

        // tauDominators fluctuates between 2 and 5
        if (deltaTimeFeedback < 0.0f) {
            tauDominatorTarget  = update(tauDominatorTarget, 2.0f, totalTime, 3f, elapsed);
            tauDominatorResting = update(tauDominatorResting, 5.0f, totalTime, 3f, elapsed);
        } else {
            tauDominatorTarget = update(tauDominatorTarget, 5.0f, totalTime, 3f, elapsed);
            tauDominatorResting = update(tauDominatorResting, 2.0f, totalTime, 3f, elapsed);
        }

        try {
            model.lock(index);
            model.setTargetRate(index, tauDominatorTarget);
            model.setRestingRate(index, tauDominatorTarget);
            model.setLastUpdateTime(index, currentTime);
        } finally {
            model.unlock(index);
        }
    }

    /**
     * Alpha function / Euler integration
     */
    private float update(float currentValue, float targetValue, float maxTimeRange, float tauDominator, float elapsed) {
        float tau = maxTimeRange / tauDominator;
        float alpha = elapsed / (tau + elapsed);

        return currentValue + (targetValue - currentValue) * alpha;
    }

}
