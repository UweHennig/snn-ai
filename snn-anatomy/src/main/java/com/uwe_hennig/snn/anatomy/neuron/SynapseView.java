/**
 * @(#)SynapseView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * SynapseView
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class SynapseView {
    private final long index;
    private final SynapseModel model;

    private final long fieldId;
    private final long neuronId;
    private final long targetId;
    private final long modulatorId;

    public SynapseView(long index, SynapseModel model, long fieldId, long neuronId, long targetId, long modulatorId) {
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

}
