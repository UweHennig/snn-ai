/**
 * @(#)Soma.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static com.uwe_hennig.snn.contracts.core.ElementType.SOMA;

import com.uwe_hennig.snn.anatomy.allocator.PlasticityModelManager;
import com.uwe_hennig.snn.anatomy.allocator.PotentialModelManager;
import com.uwe_hennig.snn.anatomy.allocator.SomaModelMangager;
import com.uwe_hennig.snn.anatomy.allocator.ThresholdModelManager;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityModel;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityView;
import com.uwe_hennig.snn.anatomy.neuron.PotentialModel;
import com.uwe_hennig.snn.anatomy.neuron.PotentialView;
import com.uwe_hennig.snn.anatomy.neuron.SomaModel;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdModel;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdView;
import com.uwe_hennig.snn.contracts.core.StimulusType;

/**
 * Soma TODO Refractory period
 *
 * @author Uwe Hennig
 */
public final class Soma {
    private Soma() {
    }

    public static void stimulate(int offset, int srcNeuronId, int srcType, int stimulusType, float stimulusValue) {
        if (Math.abs(stimulusValue) < 0.01) {
            return;
        }

        SomaModel model = SomaModelMangager.instance().getModel();
        int neuronId = SomaView.getNeuronId(model, offset);

        float currentTime = 1000; // TODO
        boolean forward = false;
        float actionPotential = 0.0f;

        int axonOffset = SomaView.getAxonId(model, offset);

        if (isExternalStimulus(model, offset, srcNeuronId)) {
            // Soma ignores inhibitory/excitatory, but forwards the stimulus
            // Feedback mechanisms also have inhibitory and excitatory effects
            Axon.stimulate(axonOffset, srcNeuronId, SOMA, stimulusType, stimulusValue);
            return;
        }

        PlasticityModel plasticityModel = PlasticityModelManager.instance().getModel();
        PotentialModel potentialModel = PotentialModelManager.instance().getModel();
        ThresholdModel thresholdModel = ThresholdModelManager.instance().getModel();

        int stpOffset = SomaView.getStpId(model, offset);
        int ltpOffset = SomaView.getLtpId(model, offset);
        int potOffset = SomaView.getPotentialId(model, offset);
        int thresholOffset = SomaView.getThresholdId(model, potOffset);

        SomaView.writeLock(model, offset);
        try {
            PlasticityView.updatePlasticityPotential(plasticityModel, stpOffset, currentTime);
            PlasticityView.updatePlasticityPotential(plasticityModel, ltpOffset, currentTime);
            PotentialView.decay(potentialModel, potOffset, currentTime);

            if (StimulusType.isTimeFeedback(stimulusType)) {
                int thresholdOffset = SomaView.getThresholdId(model, offset);

                ThresholdView.applyTimeFeedback(thresholdModel, thresholdOffset, stimulusValue);
                PlasticityView.applyTimeFeedback(plasticityModel, stpOffset, stimulusValue, currentTime);
                PlasticityView.applyTimeFeedback(plasticityModel, ltpOffset, stimulusValue, currentTime);
            }

            if (StimulusType.isValueFeedback(stimulusType)) {
                PlasticityView.applyValueFeedback(plasticityModel, stpOffset, stimulusValue, currentTime);
                PlasticityView.applyValueFeedback(plasticityModel, ltpOffset, stimulusValue, currentTime);
            }

            if (StimulusType.isStimulus(stimulusType)) {
                PotentialView.addPotentitial(potentialModel, potOffset, stimulusValue, currentTime);
            }

            if (StimulusType.isStimulus(stimulusType)) {
                float threshold = ThresholdView.getThreshold(thresholdModel, thresholOffset);

                if (PotentialView.canFire(potentialModel, potOffset, threshold)) {
                    forward = true;
                    float ltp = PlasticityView.getCurrentPotential(plasticityModel, ltpOffset);
                    float stp = PlasticityView.getCurrentPotential(plasticityModel, stpOffset);
                    actionPotential = (float) Math.clamp(stp + ltp, -70.0, 70.0);

                    // TODO die Potentiale müssen reduziert werden
                }
            }

        } finally {
            SomaView.writeUnlock(model, offset);
        }

        if (forward) {
            Axon.stimulate(axonOffset, neuronId, SOMA, stimulusType, actionPotential);
        }
    }

    private static boolean isExternalStimulus(SomaModel model, int offset, int srcNeuronId) {
        int neuronId = SomaView.getNeuronId(model, offset);
        if (neuronId != srcNeuronId) {
            return true;
        }
        return false;
    }

}
