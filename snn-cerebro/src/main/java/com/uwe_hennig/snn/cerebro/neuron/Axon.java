/**
 * @(#)Axon.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;

/**
 * Axon
 *
 * @author Uwe Hennig
 */
public final class Axon implements NeuronElement {
    private final AxonView      view;
    private final ModulatorView modulatorView;

    public Axon(AxonView view, ModulatorView modulatorView) {
        this.view = view;
        this.modulatorView = modulatorView;
    }

    public void stimulate(int stimulusIdentifier) {
        // TODO
    }

    @Override
    public NeuronElementType getType() {
        return NeuronElementType.AXON;
    }
}
