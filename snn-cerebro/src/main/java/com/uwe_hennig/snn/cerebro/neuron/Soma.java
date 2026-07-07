/**
 * @(#)Soma.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static com.uwe_hennig.snn.anatomy.neuron.PlasticityView.applyTimeFeedback;
import static com.uwe_hennig.snn.anatomy.neuron.PlasticityView.applyValueFeedback;
import static com.uwe_hennig.snn.anatomy.neuron.PlasticityView.getCurrentPotential;
import static com.uwe_hennig.snn.anatomy.neuron.PlasticityView.updatePlasticityPotential;
import static com.uwe_hennig.snn.anatomy.neuron.PotentialView.addPotentitial;
import static com.uwe_hennig.snn.anatomy.neuron.PotentialView.decay;
import static com.uwe_hennig.snn.anatomy.neuron.PotentialView.fire;
import static com.uwe_hennig.snn.anatomy.neuron.ThresholdView.getThreshold;
import static com.uwe_hennig.snn.contracts.core.NeuronElementType.AXON;
import static com.uwe_hennig.snn.contracts.core.NeuronElementType.SOMA;

import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.contracts.core.ViewIdentity;
import com.uwe_hennig.snn.services.NeuronElementRegistry;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * Soma
 *
 * @author Uwe Hennig
 */
public final class Soma extends ViewIdentity implements NeuronElement {
    private final SomaView view;
    private final int      thresholdViewId;
    private final int      potentialViewId;
    private final int      stpViewId;
    private final int      ltpViewId;

    public Soma(SomaView view, int thresholdViewId, int potentialViewId, int stpViewId, int ltpViewId) {
        this.view = view;
        this.stpViewId = stpViewId;
        this.ltpViewId = ltpViewId;
        this.thresholdViewId = thresholdViewId;
        this.potentialViewId = potentialViewId;
    }

    @Override
    public void stimulate(int stimulusIdentifier) {
        try {
            // Master Lock
            view.writeLock();
            float currentTime = 1000; // TODO

            float stimulusValue = StimulusService.getValue(stimulusIdentifier);
            if (StimulusService.isStimulus(stimulusIdentifier) && isExternalStimulus(stimulusIdentifier)) {
                SnnTransferservice.transfer(stimulusIdentifier);
            }

            updatePlasticityPotential(stpViewId, currentTime);
            updatePlasticityPotential(ltpViewId, currentTime);
            decay(potentialViewId, currentTime);

            if (StimulusService.isTimeFeedback(stimulusIdentifier)) {
                ThresholdView.applyTimeFeedback(thresholdViewId, stimulusValue);
                applyTimeFeedback(stpViewId, stimulusValue, currentTime);
                applyTimeFeedback(ltpViewId, stimulusValue, currentTime);
            }

            if (StimulusService.isValueFeedback(stimulusIdentifier)) {
                applyValueFeedback(stpViewId, stimulusValue, currentTime);
                applyValueFeedback(ltpViewId, stimulusValue, currentTime);
            }

            if (StimulusService.isStimulus(stimulusIdentifier)) {
                addPotentitial(potentialViewId, stimulusValue, currentTime);
                if (fire(potentialViewId, getThreshold(thresholdViewId))) {
                    float actionPotential = getCurrentPotential(ltpViewId) + getCurrentPotential(stpViewId);
                    stimulusIdentifier = StimulusService.update(stimulusIdentifier, StimulusType.STIMULUS.code(), actionPotential, view.getAxonId());
                }
            }

            SnnTransferservice.transfer(stimulusIdentifier);

        } finally {
            view.writeUnlock();
        }
    }

    @Override
    public NeuronElementType getType() {
        return SOMA;
    }

    @Override
    public int getNeuronId() {
        return view.getNeuronId();
    }

    private boolean isExternalStimulus(int stimulusIdentifier) {
        NeuronElement neuronElement = NeuronElementRegistry.instance().getNeuronElement(stimulusIdentifier, AXON);
        return neuronElement != null && neuronElement.getNeuronId() != this.getNeuronId();
    }

    @Override
    public int getViewId() {
        return view.getViewId();
    }
}
