/**
 * @(#)NeuronBuilder.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.contracts;

/**
 * NeuronBuilder
 *
 * @author Uwe Hennig
 */
public interface NeuronBuilder {
    // TODO DataSupport for Dendrites, Soma, Axon, Synapse
    NeuronBuilder withDendrites(int dendrites);
    NeuronBuilder withSynapses(int synapses);
    NeuronGraph build();
}
