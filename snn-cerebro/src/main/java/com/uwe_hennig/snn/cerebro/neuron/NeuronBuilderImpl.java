/**
 * @(#)NeuronBuilderImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import java.util.ArrayList;
import java.util.List;

import com.uwe_hennig.snn.anatomy.allocator.AxonAllocator;
import com.uwe_hennig.snn.anatomy.allocator.DendritAllocator;
import com.uwe_hennig.snn.anatomy.allocator.SomaAllocator;
import com.uwe_hennig.snn.anatomy.allocator.SynapseAllocator;
import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.SynapseView;
import com.uwe_hennig.snn.cerebro.contracts.NeuronBuilder;
import com.uwe_hennig.snn.cerebro.contracts.NeuronGraph;

/**
 * NeuronBuilderImpl
 * TODO NeuronAllocator, NeuronGraph, DomainObjects, WeightAllocator
 *
 * @author Uwe Hennig
 */
public class NeuronBuilderImpl implements NeuronBuilder {
    private final int fieldId;
    private final int neuronId;

    private int dendrites;
    private int synapses;

    public NeuronBuilderImpl(int fieldId, int neuronId) {
        if (DendritAllocator.instance() == null) {
            throw new IllegalStateException("DendritAllocator not created!");
        }
        if (SomaAllocator.instance() == null) {
            throw new IllegalStateException("SomaAllocator not created!");
        }
        if (AxonAllocator.instance() == null) {
            throw new IllegalStateException("AxonAllocator not created!");
        }
        if (SynapseAllocator.instance() == null) {
            throw new IllegalStateException("SynapseAllocator not created!");
        }
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

        AxonView axonView = AxonAllocator.instance().newAxonView(fieldId, neuronId);
        SomaView somaView = SomaAllocator.instance().newSomaView(fieldId, neuronId, axonView.getViewId());

        for (int i = 0; i < dendrites; i++) {
            DendritView dendritView = DendritAllocator.instance().newDendritView(fieldId, neuronId, somaView.getViewId());
            // TODO WeightView
            Dendrit dendrit = new Dendrit(dendritView, 0/*TODO*/);
            dendritList.add(dendrit);
        }

        for (int i = 0; i < synapses; i++) {
            // TODO ModulatorView
            SynapseView synapaseView = SynapseAllocator.instance().newSynapseView(fieldId, neuronId);
            Synapse synapse = new Synapse(synapaseView, null /*TODO*/);
            synapseList.add(synapse);
        }

        // TODO: NeuronGraph neuronGraph = new NeuronGraph(fieldId, neuronId, dendritList, synapseList);

        return null;
    }

}
