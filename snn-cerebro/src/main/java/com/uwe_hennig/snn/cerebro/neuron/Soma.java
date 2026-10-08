/**
 * @(#)Soma.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static com.uwe_hennig.snn.contracts.core.ElementType.AXON;

import com.uwe_hennig.snn.anatomy.allocator.SomaModelMangager;
import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.anatomy.neuron.SomaModel;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.ElementType;
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.services.NeuronElementRegistry;

/**
 * Soma
 * TODO Refractory period
 * @author Uwe Hennig
 */
public final class Soma {
    private Soma() {
    }

    public static void stimulate(int offset, int srcNeuronId, int srcType, int stimulusType, float stimulusValue) {
        SomaModel model = SomaModelMangager.instance().getModel();

            float currentTime = 1000; // TODO
            float adjustedValue = stimulusValue;

            if (StimulusType.isStimulus(stimulusType) && isExternalStimulus(model, offset, srcNeuronId)) {
                // Soma ignores inhibitory/excitatory
//              SnnTransferservice.transfer(stimulusIdentifier);
            }

            SomaView.writeLock(model, offset);
            try {
//            PotentialView.updatePlasticityPotential(stpViewId, currentTime);
//            PotentialView.updatePlasticityPotential(ltpViewId, currentTime);
//            PotentialView.decay(potentialViewId, currentTime);

            if (StimulusType.isTimeFeedback(stimulusType)) {
//                ThresholdView.applyTimeFeedback(thresholdViewId, stimulusValue);
//                PotentialView.applyTimeFeedback(stpViewId, stimulusValue, currentTime);
//                PotentialView.applyTimeFeedback(ltpViewId, stimulusValue, currentTime);
            }

            if (StimulusType.isValueFeedback(stimulusType)) {
//                PotentialView.applyValueFeedback(stpViewId, stimulusValue, currentTime);
//                PotentialView.applyValueFeedback(ltpViewId, stimulusValue, currentTime);
            }

            if (StimulusType.isStimulus(stimulusType)) {
//                PotentialView.addPotentitial(potentialViewId, stimulusValue, currentTime);
//                if (PotentialView.fire(potentialViewId, getThreshold(thresholdViewId))) {
//                    float actionPotential = getCurrentPotential(ltpViewId) + getCurrentPotential(stpViewId);
//                    stimulusIdentifier = StimulusService.update(stimulusIdentifier, StimulusType.STIMULUS.code(), actionPotential, SomaView.getAxonId(viewId));
            }
        } finally {
            SomaView.writeUnlock(model, offset);
        }

        int neuronId = SomaView.getNeuronId(model, offset);
        int axonId = SomaView.getAxonId(model, offset);

        // TODO Wert prüfen/ändern von stimulusValue
        int axonOffset = SomaView.getAxonId(model, offset);
        Axon.stimulate(axonOffset, neuronId, SomaType, stimulusType, adjustedValue);
    }

    private static boolean isExternalStimulus(SomaModel model, int offset, int srcNeuronId) {
        int neuronId = SomaView.getNeuronId(model, offset);
        if (neuronId != srcNeuronId) {
            return true;
        }
        return false;
    }

}
