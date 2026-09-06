/**
 * @(#)Soma.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static com.uwe_hennig.snn.contracts.core.NeuronElementType.AXON;
import static com.uwe_hennig.snn.contracts.core.NeuronElementType.SOMA;

import com.uwe_hennig.snn.anatomy.neuron.PlasticityView;
import com.uwe_hennig.snn.anatomy.neuron.PotentialView;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;
import com.uwe_hennig.snn.contracts.core.ViewIdentity;
import com.uwe_hennig.snn.services.NeuronElementRegistry;

/**
 * Soma
 *
 * @author Uwe Hennig
 */
public final class Soma extends ViewIdentity implements NeuronElement {
    private final SomaView somaView;
    private final ThresholdView thresholdView;
    private final PotentialView potentialView;
    private final PlasticityView stpView;
    private final PlasticityView ltpView;

    public Soma(SomaView somaView, ThresholdView thresholdView, PotentialView potentialView, PlasticityView stpView, PlasticityView ltpView) {
        this.somaView = somaView;
        this.stpView = stpView;
        this.ltpView = ltpView;
        this.potentialView = potentialView;
        this.thresholdView = thresholdView;
    }

    @Override
    public void stimulate(int stimulusIdentifier) {
        try {
            // Master Lock
            somaView.writeLock();
// TODO
//            float currentTime = 1000; // TODO
//
//            float stimulusValue = StimulusService.getValue(stimulusIdentifier);
//            if (StimulusService.isStimulus(stimulusIdentifier) && isExternalStimulus(stimulusIdentifier)) {
//                SnnTransferservice.transfer(stimulusIdentifier);
//            }
//
//            updatePlasticityPotential(stpViewId, currentTime);
//            updatePlasticityPotential(ltpViewId, currentTime);
//            decay(potentialViewId, currentTime);
//
//            if (StimulusService.isTimeFeedback(stimulusIdentifier)) {
//                ThresholdView.applyTimeFeedback(thresholdViewId, stimulusValue);
//                applyTimeFeedback(stpViewId, stimulusValue, currentTime);
//                applyTimeFeedback(ltpViewId, stimulusValue, currentTime);
//            }
//
//            if (StimulusService.isValueFeedback(stimulusIdentifier)) {
//                applyValueFeedback(stpViewId, stimulusValue, currentTime);
//                applyValueFeedback(ltpViewId, stimulusValue, currentTime);
//            }
//
//            if (StimulusService.isStimulus(stimulusIdentifier)) {
//                addPotentitial(potentialViewId, stimulusValue, currentTime);
//                if (fire(potentialViewId, getThreshold(thresholdViewId))) {
//                    float actionPotential = getCurrentPotential(ltpViewId) + getCurrentPotential(stpViewId);
//                    stimulusIdentifier = StimulusService.update(stimulusIdentifier, StimulusType.STIMULUS.code(), actionPotential, SomaView.getAxonId(viewId));
//                }
//            }
//
//            SnnTransferservice.transfer(stimulusIdentifier);

        } finally {
            somaView.writeUnlock();
        }
    }

    @Override
    public NeuronElementType getType() {
        return SOMA;
    }

    @Override
    public int getNeuronId() {
        return somaView.getNeuronId();
    }

    private boolean isExternalStimulus(int stimulusIdentifier) {
        NeuronElement neuronElement = NeuronElementRegistry.instance().getNeuronElement(stimulusIdentifier, AXON);
        return neuronElement != null && neuronElement.getNeuronId() != this.getNeuronId();
    }

    public int getTargetId() {
        return somaView.getAxonId();
    }

    @Override
    public int getViewId() {
        return somaView.getId();
    }
}
