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
        } finally {
            model.unlock(index);
        }

        return newPot;
    }

    /**
     * Alpha function / Euler integration
     */
    private float update(float currentPot, float targetPot, float totalTime, float tauDominator, float elapsed) {
        float tau = totalTime / tauDominator;
        float alpha = elapsed / (tau + elapsed);

        return currentPot + (targetPot - currentPot) * alpha;
    }

    private float updateRates() {
        // TODO calculate VZ_RESTING_RATE, VZ_TARGET_RATE
        return 3f;
    }

}
