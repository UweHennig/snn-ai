/**
 * @(#)AxonView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * AxonView
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class AxonView {
    private final long index;
    private final AxonModel model;

    private final long fieldId;
    private final long neuronId;
    private final long modulatorId;
    private final long synapseRef;

    public AxonView(long index, AxonModel model, long fieldId, long neuronId, long modulatorId, long synapseRef) {
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

}
