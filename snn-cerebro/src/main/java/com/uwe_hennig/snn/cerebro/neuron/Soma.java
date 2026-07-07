/**
 * @(#)Soma.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static com.uwe_hennig.snn.anatomy.neuron.PotentialView.addPotentitial;
import static com.uwe_hennig.snn.anatomy.neuron.PotentialView.decay;
import static com.uwe_hennig.snn.anatomy.neuron.PotentialView.fire;
import static com.uwe_hennig.snn.contracts.core.NeuronElementType.AXON;
import static com.uwe_hennig.snn.contracts.core.NeuronElementType.SOMA;

import com.uwe_hennig.snn.anatomy.neuron.PlasticityView;
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
    private final SomaView       view;
    private final PlasticityView stpView;
    private final PlasticityView ltpView;
    private final ThresholdView  thresholdView;
    private final int            potentialViewId;

    public Soma(SomaView view, ThresholdView thresholdView, int potentialViewId, PlasticityView stpView, PlasticityView ltpView) {
        this.view = view;
        this.stpView = stpView;
        this.ltpView = ltpView;
        this.thresholdView = thresholdView;
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
                SnnTransferservice.transfer(stimulusIdentifier, AXON.code());
            }

            stpView.updatePlasticityPotential(currentTime);
            ltpView.updatePlasticityPotential(currentTime);
            decay(potentialViewId, currentTime);

            if (StimulusService.isTimeFeedback(stimulusIdentifier)) {
                thresholdView.applyTimeFeedback(stimulusValue);
                stpView.applyTimeFeedback(stimulusValue, currentTime);
                ltpView.applyTimeFeedback(stimulusValue, currentTime);
            }

            if (StimulusService.isValueFeedback(stimulusIdentifier)) {
                stpView.applyValueFeedback(stimulusValue, currentTime);
                ltpView.applyValueFeedback(stimulusValue, currentTime);
            }

            if (StimulusService.isStimulus(stimulusIdentifier)) {
                addPotentitial(potentialViewId, stimulusValue, currentTime);
                if (fire(potentialViewId, thresholdView.getThreshold())) {
                    float actionPotential = ltpView.getCurrentPotential() + stpView.getCurrentPotential();
                    stimulusIdentifier = StimulusService.update(stimulusIdentifier, StimulusType.STIMULUS.code(), view.getViewId(), view.getAxonId(), -1,
                        AXON.code(), actionPotential);
                }
            }

            SnnTransferservice.transfer(stimulusIdentifier, AXON.code());

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
