/**
 * @(#)PotentialView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * PotentialView
 *
 * @author Uwe Hennig
 */
public final class PotentialView {
    private final int            index;
    private final PotentialModel model;

    public PotentialView(int index, PotentialModel model) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;

        initData();
    }

    public PotentialModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    public float decay(float currentTime) {
        float currentPotential = model.getPotential(index);
        float lastUpdate = model.getLastUpdateTime(index);
        float elapsed = Math.max(currentTime - lastUpdate, 0f);
        float repolarizationTime = model.getRepolarizationTime(index);
        float restingPotential = model.getRestingPotential(index);

        float potential = currentPotential + (repolarizationTime - currentPotential) * Math.clamp(elapsed / restingPotential, 0.0f, 1.0f);

        try {
            model.lock(index);
            model.setPotential(index, potential);
            model.setLastUpdateTime(index, currentPotential);
        } finally {
            model.unlock(index);
        }

        return potential;
    }

    public float addPotentital(float potential, float currentTime) {
        return withPotential(potential + model.getPotential(index), currentTime);
    }

    public float withActionPotential(float actionPotential, float currentTime) {
        float newPot = Math.max(model.getRestingPotential(index), model.getPotential(index) - actionPotential);
        return withPotential(newPot, currentTime);
    }

    public float getPotentital() {
        return model.getPotential(index);
    }

    public float getRestingPotential() {
        return model.getRestingPotential(index);
    }

    public float getLastUpdateTime() {
        return model.getLastUpdateTime(index);
    }

    public float getRopolarizationTime() {
        return model.getRepolarizationTime(index);
    }

    private float withPotential(float potential, float currentTime) {
        try {
            model.lock(index);
            model.setPotential(index, potential);
            model.setLastUpdateTime(index, currentTime);
        } finally {
            model.unlock(index);
        }
        return model.getPotential(index);
    }

    private void initData() {
        try {
            model.lock(index);
            // TODO fetch values from parameter service
            model.setPotential(index, -40f);
            model.setRepolarizationTime(index, 100f);
            model.setRestingPotential(index, -40);
        } finally {
            model.unlock(index);
        }
    }

}
