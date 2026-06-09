/**
 * @(#)Dendrit.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.WeightView;

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
        // TODO complete implementation
        // TODO StimulusService service; service.get(stimulusIdentifier);
        // TODO float weight = view.getWeightView().applyStimulus(stimulusValue, time);
    }
}
