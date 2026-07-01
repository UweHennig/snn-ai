/**
 * @(#)AxonView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * AxonView
 *
 * @author Uwe Hennig
 */
public final class AxonView {
    private final int       index;
    private final AxonModel model;

    public AxonView(int index, AxonModel model, ModulatorView modulatorView, MultiList multilist) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
    }

    public AxonModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    public int getSynapseRef() {
        return model.getSynapseRef(index);
    }

    public int getNeuronId() {
        return model.getNeuronId(index);
    }

    public int getModulatorId() {
        return model.getModulatorId(index);
    }

    public void setStructure(int fieldId, int neuronId, int synapseRef) {
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setSynapseRef(index, synapseRef);
        } finally {
            model.writeUnlock(index);
        }
    }
}
