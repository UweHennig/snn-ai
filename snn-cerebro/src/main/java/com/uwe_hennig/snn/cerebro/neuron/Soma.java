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
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * Soma
 *
 * @author Uwe Hennig
 */
public final class Soma implements NeuronElement {
    private final SomaView       view;
    private final PotentialView  potentialView;
    private final PlasticityView stpView;
    private final PlasticityView ltpView;
    private final ThresholdView  thresholdView;

    public Soma(SomaView view, ThresholdView thresholdView, PotentialView potentialView, PlasticityView stpView, PlasticityView ltpView) {
        this.view = view;
        this.potentialView = potentialView;
        this.stpView = stpView;
        this.ltpView = ltpView;
        this.thresholdView = thresholdView;
    }

    @Override
    public void stimulate(int stimulusIdentifier) {
        float currentTime = 1000; // TODO
        float stimulusValue = StimulusService.getValue(stimulusIdentifier);

        stpView.updatePlasticityPotential(currentTime);
        ltpView.updatePlasticityPotential(currentTime);
        potentialView.decay(currentTime);

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
            potentialView.addPotentitial(stimulusValue, currentTime);
            if (potentialView.getPotentital() > thresholdView.getThreshold()) {
                float actionPotential = ltpView.getCurrentPotential() + stpView.getCurrentPotential();
                stimulusIdentifier = StimulusService.update(stimulusIdentifier, StimulusType.STIMULUS.code(), view.getViewId(), view.getAxonId(), -1,
                    AXON.code(), actionPotential);
            }
        }

        SnnTransferservice.transfer(stimulusIdentifier, AXON.code());
    }

    @Override
    public NeuronElementType getType() {
        return SOMA;
    }

    @Override
    public int getNeuronId() {
        return view.getNeuronId();
    }

}
