/**
 * @(#)AxonView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * AxonView
 *
 * @author Uwe Hennig
 */
public final class AxonView {
    private final int       index;
    private final AxonModel model;

    private final int fieldId;
    private final int neuronId;
    private final int modulatorId;
    private final int synapseRef;

    public AxonView(int index, AxonModel model, int fieldId, int neuronId, int modulatorId, int synapseRef) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.fieldId = fieldId;
        this.neuronId = neuronId;
        this.modulatorId = modulatorId;
        this.synapseRef = synapseRef;

        initData();
    }

    // TODO remove
    private void initData() {
        try {
            model.lock(index);
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setModulatorId(index, modulatorId);
            model.setSynapseRef(index, synapseRef);
        } finally {
            model.unlock(index);
        }
    }

    public AxonModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    public int getFieldId() {
        return model.getFiedlId(index);
    }

    public int getNeuronId() {
        return model.getNeuronId(index);
    }

    public int getModulatorId() {
        return model.getModulatorId(index);
    }

    public int getSynapseRef() {
        return model.getSynapseRef(index);
    }
}
