/**
 * @(#)NeuronBuilderImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import java.util.ArrayList;
import java.util.List;

import com.uwe_hennig.snn.cerebro.contracts.NeuronBuilder;
import com.uwe_hennig.snn.cerebro.contracts.NeuronGraph;

/**
 * NeuronBuilderImpl TODO NeuronAllocator, NeuronGraph, DomainObjects, WeightAllocator
 *
 * @author Uwe Hennig
 */
public class NeuronBuilderImpl implements NeuronBuilder {
    private final int fieldId;
    private final int neuronId;

    private int dendrites;
    private int synapses;

    public NeuronBuilderImpl(int fieldId, int neuronId) {
        this.fieldId = fieldId;
        this.neuronId = neuronId;
    }

    @Override
    public NeuronBuilder withDendrites(int dendrites) {
        if (dendrites < 1) {
            throw new IllegalStateException("At least one dendrit is required!");
        }
        this.dendrites = dendrites;
        return this;
    }

    @Override
    public NeuronBuilder withSynapses(int synapses) {
        if (synapses < 1) {
            throw new IllegalStateException("At least one synapse is required!");
        }
        this.synapses = synapses;
        return this;
    }

    @Override
    public NeuronGraph build() {
        List<Dendrit> dendritList = new ArrayList<>(dendrites);
        List<Synapse> synapseList = new ArrayList<>(synapses);

        // TODO: NeuronGraph neuronGraph = new NeuronGraph(fieldId, neuronId, dendritList, synapseList);

        return null;
    }

}
