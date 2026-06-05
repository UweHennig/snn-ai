/**
 * @(#)Synapse.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.neuron.SynapseView;

/**
 * Synapse
 * @author Uwe Hennig
 */
public final class Synapse {
    private final SynapseView view;

    public Synapse(SynapseView view) {
        this.view = view;
    }

    public void stimulate(int stimulusIdentifier) {
        // TODO
    }
}
