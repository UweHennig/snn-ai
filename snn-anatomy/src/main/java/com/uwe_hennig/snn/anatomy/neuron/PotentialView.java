/// @(#)PotentialView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// PotentialView
///
/// @author Uwe Hennig
///
public final class PotentialView {
    private PotentialView() {
    }

    // ----- Domain Logic -----

    public static float decay(PotentialModel model, int offset, float currentTime) {
        float currentPotential = model.getPotential(offset);
        float lastUpdate = model.getLastUpdateTime(offset);
        float elapsed = Math.max(currentTime - lastUpdate, 0f);
        float repolarizationTime = model.getRepolarizationTime(offset);
        float restingPotential = model.getRestingPotential(offset);

        float potential = currentPotential + (repolarizationTime - currentPotential) * Math.clamp(elapsed / restingPotential, 0.0f, 1.0f);

        model.setPotential(offset, potential);
        model.setLastUpdateTime(offset, currentPotential);
        return potential;
    }

    public static float getPotential(PotentialModel model, int offset) {
        return model.getPotential(offset);
    }

    public static float addPotentitial(PotentialModel model, int offset, float potential, float currentTime) {
        return withPotential(model, offset, potential + model.getPotential(offset), currentTime);
    }

    public static boolean canFire(PotentialModel model, int offset, float threshold) {
        return model.getPotential(offset) > threshold;
    }

    private static float withPotential(PotentialModel model, int offset, float potential, float currentTime) {
        model.setPotential(offset, potential);
        model.setLastUpdateTime(offset, currentTime);
        return model.getPotential(offset);
    }

    public static int claimSlot(PotentialModel model) {
        return model.claimSlot();
    }
}
