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
    private final PlasticityView plasticityView;
    private final ThresholdView  thresholdView;
    private final ModulatorView  modulatorView;

    public Soma(SomaView view, ThresholdView  thresholdView, PlasticityView plasticityView, ModulatorView modulatorView) {
        this.view = view;
        this.plasticityView = plasticityView;
        this.modulatorView = modulatorView;
        this.thresholdView = thresholdView;
    }

    @Override
    public void stimulate(int stimulusIdentifier) {
        float currentTime = 1000; // TODO
        float stimulusValue = StimulusService.getValue(stimulusIdentifier);
        int stimulusType = StimulusService.getType(stimulusIdentifier);

        // TODO complete implementation

        StimulusService.update(stimulusIdentifier, view.getViewId(), view.getAxonId(), -1, AXON.code(), stimulusValue);
        SnnTransferservice.transfer(stimulusIdentifier, AXON.code());
    }

    @Override
    public NeuronElementType getType() {
        return SOMA;
    }
}
