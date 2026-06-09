/**
 * @(#)Dendrit.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.WeightView;
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.services.StimulusService;

/**
 * Dendrit
 *
 * @author Uwe Hennig
 */
public final class Dendrit {
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
    public void stimulate(int stimulusIdentifier) {
        float currentTime = 1000; // TODO
        float stimulusValue = StimulusService.getValue(stimulusIdentifier);
        int stimulusType = StimulusService.getType(stimulusIdentifier);

        if (StimulusType.STIMULUS.code() == stimulusType) {
            stimulusValue = weightView.applyStimulus(stimulusValue, currentTime);
            StimulusService.update(stimulusIdentifier,
                view.getViewId(), view.getSomaId(), stimulusType, stimulusValue);
        }

        if (StimulusType.TIME_FEEDBACK.code() == stimulusType) {
            weightView.applyFeedback(stimulusValue);
        }

    }
}
