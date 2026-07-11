/**
 * @(#)NeuronBuilderImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import java.util.ArrayList;
import java.util.List;

import com.uwe_hennig.snn.anatomy.allocator.AxonModelManager;
import com.uwe_hennig.snn.anatomy.allocator.SynapseListManager;
import com.uwe_hennig.snn.anatomy.allocator.DendritListManager;
import com.uwe_hennig.snn.anatomy.allocator.DendritModelManager;
import com.uwe_hennig.snn.anatomy.allocator.EdgeModelManager;
import com.uwe_hennig.snn.anatomy.allocator.ModulatorModelManager;
import com.uwe_hennig.snn.anatomy.allocator.NeuronModelManager;
import com.uwe_hennig.snn.anatomy.allocator.PlasticityModelManager;
import com.uwe_hennig.snn.anatomy.allocator.PotentialModelManager;
import com.uwe_hennig.snn.anatomy.allocator.SomaModelMangager;
import com.uwe_hennig.snn.anatomy.allocator.SynapseModelManager;
import com.uwe_hennig.snn.anatomy.allocator.ThresholdModelManager;
import com.uwe_hennig.snn.anatomy.allocator.WeightModelManager;
import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.NeuronView;
import com.uwe_hennig.snn.anatomy.neuron.PotentialView;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.SynapseView;
import com.uwe_hennig.snn.anatomy.neuron.WeightView;
import com.uwe_hennig.snn.cerebro.contracts.NeuronBuilder;
import com.uwe_hennig.snn.cerebro.contracts.NeuronGraph;

/**
 * NeuronBuilderImpl
 * TODO NeuronAllocator, NeuronGraph, DomainObjects, WeightAllocator, EdgeModel
 *
 * @author Uwe Hennig
 */
public class NeuronBuilderImpl implements NeuronBuilder {
    private int fieldId;
    private int neuronId;

    private int dendrites;
    private int synapses;

    public NeuronBuilderImpl(int fieldId) {
        this.fieldId = fieldId;
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
        if (!checkPreconditions()) {
            throw new IllegalStateException("System not ready!");
        }

        this.neuronId = NeuronModelManager.instance().nextId();

        List<Dendrit> dendritList = new ArrayList<>(dendrites);
        List<Synapse> synapseList = new ArrayList<>(synapses);

        // TODO MultiListManger
        int synapsesRef = SynapseListManager.instance().nextId();
        int [] synapseIdArray = new int [synapses];
        for (int i = 0; i < synapses; i++) {
            Synapse synapse = createSynapse();
            synapseList.add(synapse);
            synapseIdArray[i] = synapse.getViewId();
        }
        SynapseListManager.instance().getModel().put(synapsesRef, synapseIdArray);

        Axon axon = createAxon(synapsesRef);
        int axonId = axon.getViewId();

        Soma soma = createSoma(axon.getViewId());
        int somaId = soma.getViewId();

        int dendritRef = DendritListManager.instance().nextId();
        int [] dendritArray = new int [dendrites];

        for (int i = 0; i < dendrites; i++) {
            Dendrit dendrit = createDendrit(soma.getViewId());
            dendritList.add(dendrit);
            dendritArray[i] = dendrit.getViewId();
        }
        DendritListManager.instance().getModel().put(dendritRef, dendritArray);

        NeuronView.setRefs(neuronId, fieldId, dendritRef, somaId, axonId, synapsesRef);

        return new NeuronGraph(fieldId, neuronId, soma, axon, dendritList, synapseList);
    }

    private boolean checkPreconditions() {
        boolean result = true;

        DendritModelManager dmm = DendritModelManager.instance();
        if (dmm == null) {
            System.err.println("DendritModelManager not instantiated!");
            result = false;
        }

        WeightModelManager wmm = WeightModelManager.instance();
        if (wmm == null) {
            System.err.println("WeightModelManager not instantiated!");
            result = false;
        }

        SomaModelMangager somm = SomaModelMangager.instance();
        if (somm == null) {
            System.err.println("SomaModelMangager not instantiated!");
            result = false;
        }

        PotentialModelManager pmm = PotentialModelManager.instance();
        if (pmm == null) {
            System.err.println("PotentialModelManager not instantiated!");
            result = false;
        }

        ThresholdModelManager tmm = ThresholdModelManager.instance();
        if (tmm == null) {
            System.err.println("ThresholdModelManager not instantiated!");
            result = false;
        }

        PlasticityModelManager plmm = PlasticityModelManager.instance();
        if (plmm == null) {
            System.err.println("PlasticityModelManager not instantiated!");
            result = false;
        }

        AxonModelManager amm = AxonModelManager.instance();
        if (amm == null) {
            System.err.println("AxonModelManager not instantiated!");
            result = false;
        }

        SynapseModelManager symm = SynapseModelManager.instance();
        if (symm == null) {
            System.err.println("SynapseModelManager not instantiated!");
            result = false;
        }

        ModulatorModelManager mmm = ModulatorModelManager.instance();
        if (mmm == null) {
            System.err.println("ModulatorModelManager not instantiated!");
            result = false;
        }

        SynapseListManager asmm = SynapseListManager.instance();
        if (asmm == null) {
            System.err.println("AxonSynapseModelManager not instantiated!");
            result = false;
        }

        EdgeModelManager emm = EdgeModelManager.instance();
        if (emm == null) {
            System.err.println("EdgeModelManager not instantiated!");
            result = false;
        }

        return result;
    }

    private Dendrit createDendrit(int somaId) {
        try {
            DendritModelManager dmm = DendritModelManager.instance();
            int dendritId = dmm.nextId();
            DendritView.setStructure(dendritId, fieldId, neuronId, somaId);

            WeightModelManager wmm = WeightModelManager.instance();
            int weightId = wmm.nextId();
            WeightView.initDefaultValues(weightId);

            // TODO EdgeView.setSingleEdge(modulatorId, modulatorId, modulatorId, synapseId, modulatorId);


            return new Dendrit(dendritId, weightId);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception in createDendrit " + e.getLocalizedMessage());
            return null;
        }
    }

    private Soma createSoma(int axonId) {
        SomaModelMangager somm = SomaModelMangager.instance();
        int somaId = somm.nextId();
        SomaView.setStructure(somaId, fieldId, neuronId, axonId);

        ThresholdModelManager tmm = ThresholdModelManager.instance();
        int thresholdId = tmm.nextId();

        PotentialModelManager pmm = PotentialModelManager.instance();
        int potentialId = pmm.nextId();
        PotentialView.initData(potentialId);

        PlasticityModelManager plmm = PlasticityModelManager.instance();
        int stpId = plmm.nextId();
        int ltpId = plmm.nextId();

        // TODO EdgeView.setSingleEdge(modulatorId, modulatorId, modulatorId, synapseId, modulatorId);

        return new Soma(somaId, thresholdId, potentialId, stpId, ltpId);
    }

    private Axon createAxon(int synapseRef) {
        AxonModelManager amm = AxonModelManager.instance();
        int axonId = amm.nextId();

        ModulatorModelManager mmm = ModulatorModelManager.instance();
        int modulatorId = mmm.nextId();

        AxonView.setStructure(axonId, fieldId, neuronId, synapseRef, modulatorId);

        // TODO EdgeView.setMultiEdge(modulatorId, modulatorId, modulatorId, synapseId, modulatorId);

        return new Axon(axonId, modulatorId, synapseRef);
    }

    private Synapse createSynapse() {
        SynapseModelManager smm = SynapseModelManager.instance();
        int synapseId = smm.nextId();

        ModulatorModelManager mmm = ModulatorModelManager.instance();
        int modulatorId = mmm.nextId();

        SynapseView.setStructure(synapseId, fieldId, neuronId, modulatorId);

        // TODO EdgeView.setSingleEdge(modulatorId, modulatorId, modulatorId, synapseId, modulatorId);

        return new Synapse(synapseId, modulatorId);
    }
}
