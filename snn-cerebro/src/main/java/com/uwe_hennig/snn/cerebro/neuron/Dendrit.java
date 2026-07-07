/**
 * @(#)Dendrit.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.WeightView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.contracts.core.ViewIdentity;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * Dendrit
 *
 * @author Uwe Hennig
 */
public final class Dendrit extends ViewIdentity implements NeuronElement {
    private final DendritView view;
    private final int         weightIdentifier;

    public Dendrit(DendritView view, int weightIdentifier) {
        this.view = view;
        this.weightIdentifier = weightIdentifier;
    }

    /**
     * stimulate uses StimulateService to handle stimulus
     *
     * @param stimulusIdentifier
     */
    @Override
    public void stimulate(int stimulusIdentifier) {
        try {
            view.writeLock();
            float currentTime = 1000; // TODO Model time

            float stimulusValue = StimulusService.getValue(stimulusIdentifier);
            if (StimulusService.isStimulus(stimulusIdentifier)) {
                stimulusValue = WeightView.applyStimulus(weightIdentifier, stimulusValue, currentTime);
                stimulusIdentifier = StimulusService.update(stimulusIdentifier, StimulusType.STIMULUS.code(), view.getViewId(), view.getSomaId(), -1,
                    NeuronElementType.SOMA.code(), stimulusValue);
            }
            if (StimulusService.isTimeFeedback(stimulusIdentifier)) {
                WeightView.applyFeedback(weightIdentifier, stimulusValue);
            }

            SnnTransferservice.transfer(stimulusIdentifier, NeuronElementType.SOMA.code());

        } finally {
            view.writeUnlock();
        }
    }

    @Override
    public NeuronElementType getType() {
        return NeuronElementType.DENDRIT;
    }

    @Override
    public int getNeuronId() {
        return view.getNeuronId();
    }

    @Override
    public int getViewId() {
        return view.getViewId();
    }

}
