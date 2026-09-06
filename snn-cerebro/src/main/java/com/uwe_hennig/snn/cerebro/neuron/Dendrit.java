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
import com.uwe_hennig.snn.contracts.core.ViewIdentity;

/**
 * Dendrit
 *
 * @author Uwe Hennig
 */
public final class Dendrit extends ViewIdentity implements NeuronElement {
    private final int viewId;
    private final WeightView weightView;

    public Dendrit(int viewId, WeightView weightView) {
        this.viewId = viewId;
        this.weightView = weightView;
    }

    /**
     * stimulate uses StimulateService to handle stimulus
     *
     * @param stimulusIdentifier
     */
    @Override
    public void stimulate(int stimulusIdentifier) {
        try {
            DendritView.writeLock(viewId);
// TODO
//            float currentTime = 1000; // TODO Model time
//
//            float stimulusValue = StimulusService.getValue(stimulusIdentifier);
//            if (StimulusService.isStimulus(stimulusIdentifier)) {
//                stimulusValue = WeightView.applyStimulus(weightId, stimulusValue, currentTime);
//                stimulusIdentifier = StimulusService.update(stimulusIdentifier, StimulusType.STIMULUS.code(), stimulusValue, DendritView.getSomaId(viewId));
//            }
//            if (StimulusService.isTimeFeedback(stimulusIdentifier)) {
//                WeightView.applyFeedback(weightId, stimulusValue);
//            }
//
//            SnnTransferservice.transfer(stimulusIdentifier);

        } finally {
            DendritView.writeUnlock(viewId);
        }
    }

    @Override
    public NeuronElementType getType() {
        return NeuronElementType.DENDRIT;
    }

    @Override
    public int getNeuronId() {
        return DendritView.getNeuronId(viewId);
    }

    public int getTargetId() {
        return DendritView.getSomaId(viewId);
    }

    @Override
    public int getViewId() {
        return viewId;
    }

}
