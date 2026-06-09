/**
 * @(#)Synapse.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.SynapseView;

/**
 * Synapse
 *
 * @author Uwe Hennig
 */
public final class Synapse {
    private final SynapseView   view;
    private final ModulatorView modulatorView;

    public Synapse(SynapseView view, ModulatorView modulatorView) {
        this.view = view;
        this.modulatorView = modulatorView;
    }

    public void stimulate(int stimulusIdentifier) {
        // TODO
    }
}
