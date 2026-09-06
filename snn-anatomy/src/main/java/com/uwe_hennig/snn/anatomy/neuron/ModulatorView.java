/**
 * @(#)ModulatorView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.ModulatorModelManager;

/**
 * ModulatorView
 *
 * @author Uwe Hennig
 */
public final class ModulatorView {
    static final float TARGET_INHIBITORY = 0.9f;
    static final float TARGET_EXCITATORY = 1.1f;

    static final float POTENTIAL_RANGE = 70f;
    static final float ALPHA_FACTOR    = 0.01f;

    private final ModulatorModel model;
    private final int index;

    public ModulatorView(ModulatorModel model, int index) {
        this.model = model;
        this.index = index;
    }

    public int getId() {
        return index;
    }

    // ----- Domain Logic -----

    // The method is called on normal stimuli
    public float applyStimulus(float stimulus, float currentTime) {
        if (!relevantGain(currentTime)) {
            // No influence
            return stimulus;
        }

        // Calculate the stimulus based on the influencing factor
        float modulationGain = model.getModulationGain(index);
        float result = stimulus * modulationGain;
        result = Math.clamp(result, 0, POTENTIAL_RANGE);

        return result;
    }

    // The method is called ony on inhibitory/excitatory events
    public void applyModulation(float potential, float currentTime, boolean inhibitory) {
        ModulatorModel model = ModulatorModelManager.instance().getModel();

        // Recalculate the influence factor
        float deltaNormalized = Math.clamp(potential / POTENTIAL_RANGE, 0f, 1f);
        float alpha = ALPHA_FACTOR * deltaNormalized;

        float modulationGain = model.getModulationGain(index);
        if (inhibitory) {
            modulationGain += (TARGET_INHIBITORY - modulationGain) * alpha;
        } else {
            modulationGain += (TARGET_EXCITATORY - modulationGain) * alpha;
        }

        model.setModulationGain(index, modulationGain);
        model.setLastEventTime(index, currentTime);
    }

    // ----- convenience -----

    private boolean relevantGain(float currentTime) {
        ModulatorModel model = ModulatorModelManager.instance().getModel();
        float lastEventTime = model.getLastEventTime(index);
        float deltaTime = currentTime - lastEventTime;
        float duration = model.getGainDuration(index);
        if (deltaTime > duration) {
            return false;
        }
        return true;
    }
}
