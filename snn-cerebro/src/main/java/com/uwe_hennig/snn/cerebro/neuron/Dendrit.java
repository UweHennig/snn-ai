/**
 * @(#)Dendrit.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.neuron.DendritView;

/**
 * Dendrit
 * @author Uwe Hennig
 */
public final class Dendrit {
    private final DendritView view;

    public Dendrit(DendritView view) {
        this.view = view;
    }

    /**
     * stimulate uses StimulateService to handle stimulus
     * @param stimulusIdentifier
     */
    public void stimulate(int stimulusIdentifier) {
        // TODO complete implementation
        // TODO StimulusService service; service.get(stimulusIdentifier);
        // TODO float weight = view.getWeightView().applyStimulus(stimulusValue, time);
    }
}
