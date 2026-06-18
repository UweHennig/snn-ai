/**
 * @(#)Dendrit.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.WeightView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * Dendrit
 *
 * @author Uwe Hennig
 */
public final class Dendrit implements NeuronElement {
    private final DendritView   view;
    private final WeightView    weightView;
    private final ModulatorView modulatorView;

    public Dendrit(DendritView view, WeightView weightView, ModulatorView modulatorView) {
        this.view = view;
        this.weightView = weightView;
        this.modulatorView = modulatorView;
    }

    /**
     * stimulate uses StimulateService to handle stimulus
     *
     * @param stimulusIdentifier
     */
    @Override
    public void stimulate(int stimulusIdentifier) {
        int signalType = 0; // TODO
        float currentTime = 1000; // TODO
        float stimulusValue = StimulusService.getValue(stimulusIdentifier);
        int stimulusType = StimulusService.getType(stimulusIdentifier);

        if (StimulusType.STIMULUS.code() == stimulusType) {
            stimulusValue = weightView.applyStimulus(stimulusValue, currentTime);
            StimulusService.update(stimulusIdentifier, view.getViewId(), view.getSomaId(), stimulusType, stimulusValue);
            modulatorView.applyStimulus(stimulusValue, signalType, currentTime);
        }

        if (StimulusType.TIME_FEEDBACK.code() == stimulusType) {
            weightView.applyFeedback(stimulusValue);
        }

        SnnTransferservice.transfer(stimulusIdentifier, NeuronElementType.SOMA.code());
    }

    @Override
    public NeuronElementType getType() {
        return NeuronElementType.DENDRIT;
    }
}
