/**
 * @(#)NeuronBuilderImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.allocator.AxonModelManager;
import com.uwe_hennig.snn.anatomy.allocator.DendritModelManager;
import com.uwe_hennig.snn.anatomy.allocator.ModulatorModelManager;
import com.uwe_hennig.snn.anatomy.allocator.PlasticityModelManager;
import com.uwe_hennig.snn.anatomy.allocator.PotentialModelManager;
import com.uwe_hennig.snn.anatomy.allocator.SomaModelMangager;
import com.uwe_hennig.snn.anatomy.allocator.SynapseModelManager;
import com.uwe_hennig.snn.anatomy.allocator.ThresholdModelManager;
import com.uwe_hennig.snn.anatomy.allocator.WeightModelManager;
import com.uwe_hennig.snn.anatomy.neuron.AxonModel;
import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.anatomy.neuron.DendritModel;
import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorModel;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityModel;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityView;
import com.uwe_hennig.snn.anatomy.neuron.PotentialModel;
import com.uwe_hennig.snn.anatomy.neuron.PotentialView;
import com.uwe_hennig.snn.anatomy.neuron.SomaModel;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.SynapseModel;
import com.uwe_hennig.snn.anatomy.neuron.SynapseView;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdModel;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdView;
import com.uwe_hennig.snn.anatomy.neuron.WeightModel;
import com.uwe_hennig.snn.anatomy.neuron.WeightView;
import com.uwe_hennig.snn.cerebro.contracts.NeuronBuilder;
import com.uwe_hennig.snn.cerebro.contracts.NeuronInfo;

/**
 * NeuronBuilderImpl
 *
 * @author Uwe Hennig
 */
public class NeuronBuilderImpl implements NeuronBuilder, NeuronInfo {
    private int fieldId;
    private int neuronId;

    private int numDendrites;
    private int numSynapses;

    public NeuronBuilderImpl(int fieldId) {
        this.fieldId = fieldId;
    }

    @Override
    public NeuronBuilder withDendrites(int dendrites) {
        if (dendrites < 1) {
            throw new IllegalStateException("At least one dendrit is required!");
        }
        this.numDendrites = dendrites;
        return this;
    }

    @Override
    public NeuronBuilder withSynapses(int synapses) {
        if (synapses < 1) {
            throw new IllegalStateException("At least one synapse is required!");
        }
        this.numSynapses = synapses;
        return this;
    }

    @Override
    public NeuronInfo build() {
        if (!checkPreconditions()) {
            throw new IllegalStateException("System not ready!");
        }

        neuronId = createNeuron();

        // synapse
        int [] synapseArray = new int [numSynapses];

        for (int i = 0; i < numSynapses; i++) {
            synapseArray[i] = createSynapse(fieldId, neuronId, 0);
        }

        // axon
        int axonOffset = createAxon(fieldId, neuronId, synapseArray);

        // soma
        int somaOffset = createSoma(fieldId, neuronId, axonOffset);

        // dendrit
        int[] dendritArray = new int[numDendrites];

        for (int i = 0; i < numDendrites; i++) {
            int dendritOffset = createDendrit(fieldId, neuronId, somaOffset);
            dendritArray[i] = dendritOffset;
        }

        // TODO
        return this; // new NeuronInfo(fieldId, neuronId, somaOffset, axonOffset, dendritArray, synapseArray);
    }

    // -- convenient --

    private int createNeuron() {
        // TODO
        return 0;
    }

    private int createDendrit(int fieldId, int neuronId, int somaId) {
        try {
            DendritModel model = DendritModelManager.instance().getModel();
            int dendritOffset = DendritView.claimSlot(model, fieldId, neuronId, somaId);

            WeightModel weightModel = WeightModelManager.instance().getModel();
            int weightOffset = WeightView.claimSlot(weightModel);

            DendritView.setWeightId(model, dendritOffset, weightOffset);

            return dendritOffset;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Exception in createDendrit " + e.getLocalizedMessage());
            return -1;
        }
    }

    private int createSoma(int fieldId, int neuronId, int axonId) {
        SomaModel model = SomaModelMangager.instance().getModel();
        ThresholdModel thresholdModel = ThresholdModelManager.instance().getModel();
        PotentialModel potentialModel = PotentialModelManager.instance().getModel();
        PlasticityModel plasticityModel = PlasticityModelManager.instance().getModel();

        int thresholdOffset = ThresholdView.claim(thresholdModel);
        int potentialOffset = PotentialView.claimSlot(potentialModel);
        int stpOffset = PlasticityView.claimSlot(plasticityModel);
        int ltpOffset = PlasticityView.claimSlot(plasticityModel);
        int offset = SomaView.claimSlot(model, fieldId, neuronId, axonId);

        SomaView.setThresholdId(model, offset, thresholdOffset);
        SomaView.setPotentialId(model, offset, potentialOffset);
        SomaView.setStpId(model, offset, stpOffset);
        SomaView.setLtpId(model, offset, ltpOffset);

        return offset;
    }

    private int createAxon(int fieldOffset, int neuronOffset, int [] numSynapses) {
        AxonModel model = AxonModelManager.instance().getModel();
        ModulatorModel modulatorModel = ModulatorModelManager.instance().getModel();
        int modulatorOffset = ModulatorView.claimSlot(modulatorModel);

        // TODO BufferedTransferManger
        // BufferedTransferSegment: int allocateBlock(int entries, int srcNeuronId, int srcId, int srcType)

        int offset = AxonView.claimSlot(model, fieldOffset, neuronOffset, neuronOffset);
        AxonView.setModulatorId(model, offset, modulatorOffset);

        return offset;
    }

    private int createSynapse(int fieldOffset, int neuronOffset, int targetOffset) {
        SynapseModel synapseModel = SynapseModelManager.instance().getModel();
        ModulatorModel modulatorModel = ModulatorModelManager.instance().getModel();

        int modulatorOffset = ModulatorView.claimSlot(modulatorModel);
        int synapseOffset = SynapseView.claimSlot(synapseModel, fieldOffset, neuronOffset, targetOffset);
        SynapseView.setModulatorId(synapseModel, synapseOffset, modulatorOffset);

        return synapseOffset;
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

        return result;
    }

}
