/// @(#)ModulatorView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// ModulatorView
///
/// @author Uwe Hennig
///
public final class ModulatorView {
    static final float TARGET_INHIBITORY = 0.9f;
    static final float TARGET_EXCITATORY = 1.1f;

    static final float POTENTIAL_RANGE = 70f;
    static final float ALPHA_FACTOR    = 0.01f;

    private ModulatorView() {
    }

    // ----- Domain Logic -----

    // The method is called on normal stimuli
    public static float applyStimulus(ModulatorModel model, int offset, float stimulus, float currentTime) {
        if (!relevantGain(model, offset, currentTime)) {
            // No influence
            return stimulus;
        }

        // Calculate the stimulus based on the influencing factor
        float modulationGain = model.getModulationGain(offset);
        float result = stimulus * modulationGain;
        result = Math.clamp(result, 0, POTENTIAL_RANGE);

        return result;
    }

    // The method is called ony on inhibitory/exitatory events
    public static void applyModulation(ModulatorModel model, int offset, float potential, float currentTime, boolean inhibitory) {
        // Recalculate the influence factor
        float deltaNormalized = Math.clamp(potential / POTENTIAL_RANGE, 0f, 1f);
        float alpha = ALPHA_FACTOR * deltaNormalized;

        float modulationGain = model.getModulationGain(offset);
        if (inhibitory) {
            modulationGain += (TARGET_INHIBITORY - modulationGain) * alpha;
        } else {
            modulationGain += (TARGET_EXCITATORY - modulationGain) * alpha;
        }

        model.setModulationGain(offset, modulationGain);
        model.setLastEventTime(offset, currentTime);
    }

    public static int claimSlot(ModulatorModel model) {
        return model.claimSlot();
    }

    // ----- convenience -----

    private static boolean relevantGain(ModulatorModel model, int offset, float currentTime) {
        float lastEventTime = model.getLastEventTime(offset);
        float deltaTime = currentTime - lastEventTime;
        float duration = model.getGainDuration(offset);

        if (deltaTime > duration) {
            return false;
        }
        return true;
    }
}
