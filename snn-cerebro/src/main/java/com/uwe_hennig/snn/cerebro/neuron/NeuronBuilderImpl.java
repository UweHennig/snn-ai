/**
 * @(#)NeuronBuilderImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import java.util.ArrayList;
import java.util.List;

import com.uwe_hennig.snn.anatomy.allocator.AxonModelManager;
import com.uwe_hennig.snn.anatomy.allocator.DendritListManager;
import com.uwe_hennig.snn.anatomy.allocator.DendritModelManager;
import com.uwe_hennig.snn.anatomy.allocator.ModulatorModelManager;
import com.uwe_hennig.snn.anatomy.allocator.NeuronModelManager;
import com.uwe_hennig.snn.anatomy.allocator.PlasticityModelManager;
import com.uwe_hennig.snn.anatomy.allocator.PotentialModelManager;
import com.uwe_hennig.snn.anatomy.allocator.SomaModelMangager;
import com.uwe_hennig.snn.anatomy.allocator.SynapseListManager;
import com.uwe_hennig.snn.anatomy.allocator.SynapseModelManager;
import com.uwe_hennig.snn.anatomy.allocator.ThresholdModelManager;
import com.uwe_hennig.snn.anatomy.allocator.WeightModelManager;
import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.anatomy.neuron.DendritModel;
import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityView;
import com.uwe_hennig.snn.anatomy.neuron.PotentialView;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.SynapseView;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdView;
import com.uwe_hennig.snn.anatomy.neuron.WeightModel;
import com.uwe_hennig.snn.cerebro.contracts.NeuronBuilder;
import com.uwe_hennig.snn.cerebro.contracts.NeuronGraph;

/**
 * NeuronBuilderImpl
 *
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

        // synapse
        List<Synapse> synapseList = new ArrayList<>(synapses);

        int[] synapseIdArray = new int[synapses];

        for (int i = 0; i < synapses; i++) {
            Synapse synapse = createSynapse();
            synapseList.add(synapse);
            synapseIdArray[i] = synapse.getViewId();
        }
        int synapsesRef = SynapseListManager.instance().nextId();
        SynapseListManager.instance().getModel().put(synapsesRef, synapseIdArray);

        // axon
        Axon axon = createAxon(synapsesRef);
        int axonId = axon.getViewId();

        // soma
        Soma soma = createSoma(axon.getViewId());
        int somaId = soma.getViewId();

        // dendrit
        int[] dendritArray = new int[dendrites];
        int[] weightArray = new int[dendrites];

        for (int i = 0; i < dendrites; i++) {
            int weighOffset = createWeight();
            int dendritOffset = createDendrit(soma.getViewId(), weighOffset);
            dendritArray[i] = dendritOffset;
            weightArray[i] = weighOffset;
        }

        // TODO neuron
        this.neuronId = NeuronModelManager.instance().nextId();
        //NeuronView.setRefs(neuronId, fieldId, dendritRef, somaId, axonId, synapsesRef);

        // TODO
        return new NeuronGraph(fieldId, neuronId, soma, axon, null /* dendritList */, synapseList);
    }

    // -- convenient --

    private int createWeight() {
        // TODO
        WeightModelManager wmm = WeightModelManager.instance();
        WeightModel model = wmm.getModel();
        int weightOffset = model.claimSlot();
        return weightOffset;
    }

    private int createDendrit(int somaId, int weightId) {
        try {
            DendritModelManager dmm = DendritModelManager.instance();
            DendritModel model = dmm.getModel();
            int dendritOffset = model.alloc(fieldId, neuronId, somaId, weightId);

            DendritView.setStructure(model, dendritOffset, fieldId, neuronId, somaId, weightId);

            return dendritOffset;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception in createDendrit " + e.getLocalizedMessage());
            return -1;
        }
    }

    private Soma createSoma(int axonId) {
        SomaModelMangager somm = SomaModelMangager.instance();
        SomaView somaView = somm.createView();
        somaView.setStructure(fieldId, neuronId, axonId);

        ThresholdModelManager tmm = ThresholdModelManager.instance();
        ThresholdView thresholdView = tmm.createView();

        PotentialModelManager pmm = PotentialModelManager.instance();
        PotentialView potentialView = pmm.createView();

        PlasticityModelManager plmm = PlasticityModelManager.instance();
        PlasticityView stpView = plmm.createView();
        PlasticityView ltpView = plmm.createView();

        return new Soma(somaView, thresholdView, potentialView, stpView, ltpView);
    }

    private Axon createAxon(int synapseRef) {
        AxonModelManager amm = AxonModelManager.instance();
        AxonView axonView = amm.createView();

        ModulatorModelManager mmm = ModulatorModelManager.instance();
        ModulatorView modulatorView = mmm.createView();

        axonView.setStructure(fieldId, neuronId, synapseRef);

        return new Axon(axonView, modulatorView);
    }

    private Synapse createSynapse() {
        SynapseModelManager smm = SynapseModelManager.instance();
        SynapseView synapseView = smm.createView();

        ModulatorModelManager mmm = ModulatorModelManager.instance();
        ModulatorView modulatorView = mmm.createView();

        synapseView.setStructure(fieldId, neuronId, modulatorView.getId());

        return new Synapse(synapseView, modulatorView);
    }

    private boolean checkPreconditions() {
        boolean result = true;

        // Dendrit
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

        DendritListManager dlm = DendritListManager.instance();
        if (dlm == null) {
            System.err.println("DendritListManager not instantiated!");
            result = false;
        }

        // Soma
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

        // Axon
        AxonModelManager amm = AxonModelManager.instance();
        if (amm == null) {
            System.err.println("AxonModelManager not instantiated!");
            result = false;
        }

        // Synapse
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
            System.err.println("SynapseListManager not instantiated!");
            result = false;
        }

        return result;
    }

}
