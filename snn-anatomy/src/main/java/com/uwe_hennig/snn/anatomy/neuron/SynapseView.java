/**
 * @(#)SynapseView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * SynapseView
 *
 * @author Uwe Hennig
 */
public class SynapseView {
    private final int          index;
    private final SynapseModel model;

    private final int fieldId;
    private final int neuronId;
    private final int targetId;
    private final int modulatorId;

    public SynapseView(int index, SynapseModel model, int fieldId, int neuronId, int targetId, int modulatorId) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.fieldId = fieldId;
        this.neuronId = neuronId;
        this.targetId = targetId;
        this.modulatorId = modulatorId;

        initData();
    }

    // TODO remove
    private void initData() {
        try {
            model.lock(index);
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setTargetId(index, targetId);
            model.setModulatorId(index, modulatorId);
        } finally {
            model.unlock(index);
        }
    }

    public SynapseModel getModel() {
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

    public int getTargetId() {
        return model.getTargetId(index);
    }

    public int getTargetType() {
        return model.getTargetType(index);
    }

    public int getModulatorId() {
        return model.getModulatorId(index);
    }

}
