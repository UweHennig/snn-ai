/**
 * @(#)Soma.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityView;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;

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

    public void stimulate(int stimulusIdentifier) {
        // TODO
    }

    @Override
    public NeuronElementType getType() {
        return NeuronElementType.SOMA;
    }
}
