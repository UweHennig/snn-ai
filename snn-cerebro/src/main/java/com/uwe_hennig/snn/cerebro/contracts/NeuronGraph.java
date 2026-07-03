/**
 * @(#)NeuronGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.contracts;

import java.util.List;

import com.uwe_hennig.snn.cerebro.neuron.Dendrit;
import com.uwe_hennig.snn.cerebro.neuron.Synapse;

/**
 * NeuronGraph
 *
 * @author Uwe Hennig
 */
public record NeuronGraph(List<Dendrit> dendrites, List<Synapse> synapses) {
}
