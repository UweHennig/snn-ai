/**
 * @(#)PotentialView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.PotentialModelManager;

/**
 * PotentialView
 *
 * @author Uwe Hennig
 */
public final class PotentialView {
    public static float decay(int index, float currentTime) {
        PotentialModel model = PotentialModelManager.instance().getModel();

        float currentPotential = model.getPotential(index);
        float lastUpdate = model.getLastUpdateTime(index);
        float elapsed = Math.max(currentTime - lastUpdate, 0f);
        float repolarizationTime = model.getRepolarizationTime(index);
        float restingPotential = model.getRestingPotential(index);

        float potential = currentPotential + (repolarizationTime - currentPotential) * Math.clamp(elapsed / restingPotential, 0.0f, 1.0f);

        model.setPotential(index, potential);
        model.setLastUpdateTime(index, currentPotential);
        return potential;
    }

    public static float getPotential(int index) {
        PotentialModel model = PotentialModelManager.instance().getModel();
        try {
            model.readLock(index);
            return model.getPotential(index);
        } finally {
            model.readUnlock(index);
        }
    }

    public static float addPotentitial(int index, float potential, float currentTime) {
        PotentialModel model = PotentialModelManager.instance().getModel();

        return withPotential(index, potential + model.getPotential(index), currentTime);
    }

    public static boolean fire(int index, float threshold) {
        PotentialModel model = PotentialModelManager.instance().getModel();
        try {
            model.readLock(index);
            return model.getPotential(index) > threshold;
        } finally {
            model.readUnlock(index);
        }
    }

    private static float withPotential(int index, float potential, float currentTime) {
        PotentialModel model = PotentialModelManager.instance().getModel();

        model.setPotential(index, potential);
        model.setLastUpdateTime(index, currentTime);
        return model.getPotential(index);
    }

    // TODO remove
    public static void initData(int index) {
        PotentialModel model = PotentialModelManager.instance().getModel();
        try {
            model.writeLock(index);
            // TODO fetch values from parameter service
            model.setPotential(index, -40f);
            model.setRepolarizationTime(index, 100f);
            model.setRestingPotential(index, -40);
        } finally {
            model.writeUnlock(index);
        }
    }
}
