/**
 * @(#)Soma.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static com.uwe_hennig.snn.contracts.core.NeuronElementType.AXON;
import static com.uwe_hennig.snn.contracts.core.NeuronElementType.SOMA;

import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityView;
import com.uwe_hennig.snn.anatomy.neuron.PotentialView;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;
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
    private final ModulatorView  modulatorView;

    public Soma(SomaView view, ThresholdView  thresholdView, PotentialView  potentialView, PlasticityView stpView, PlasticityView ltpView, ModulatorView modulatorView) {
        this.view = view;
        this.potentialView = potentialView;
        this.stpView = stpView;
        this.ltpView = ltpView;
        this.modulatorView = modulatorView;
        this.thresholdView = thresholdView;
    }

    @Override
    public void stimulate(int stimulusIdentifier) {
        // TODO complete implementation
        float currentTime = 1000; // TODO

        float stimulusValue = StimulusService.getValue(stimulusIdentifier);
        int stimulusType = StimulusService.getTrgType(stimulusIdentifier);

        if (StimulusService.isTimeFeedback(stimulusIdentifier)) {
            //thresholdView.update();
            SnnTransferservice.transfer(stimulusIdentifier, AXON.code());
        }

        if (StimulusService.isValueFeedback(stimulusIdentifier)) {
            //stpView.applyValueFeedback(, );
            //ltpView.applyValueFeedback(, );
            SnnTransferservice.transfer(stimulusIdentifier, AXON.code());
        }

        if (StimulusService.isStimulus(stimulusIdentifier)) {
            /*
            potentialView.applyValueFeedback(, );
            if (potentialView.getPotentital() > thresholdView.getValue()) {
                float actionPot = ltp.value() + stp.value();
                stimulusIdentifier = StimulusService.update(stimulusIdentifier, stimulusType, view.getViewId(), view.getAxonId(), -1, AXON.code(), actionPot);
                SnnTransferservice.transfer(stimulusIdentifier, AXON.code());
            }
            */
        }
    }

    @Override
    public NeuronElementType getType() {
        return SOMA;
    }
}
