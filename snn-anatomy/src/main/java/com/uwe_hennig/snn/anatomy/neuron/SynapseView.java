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
    private final int           index;
    private final SynapseModel  model;

    public SynapseView(int index, SynapseModel model) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
    }

    // ----- lock/unlock -----

    public void readLock() {
        model.readLock(index);
    }

    public void readUnlock() {
        model.readUnlock(index);
    }

    public void writeLock() {
        model.writeLock(index);
    }

    public void writeUnlock() {
        model.writeUnlock(index);
    }
    public SynapseModel getModel() {
        return model;
    }

    // ----- getter/setter -----

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

    public void setStructure(int fieldId, int neuronId, int targetId) {
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setTargetId(index, targetId);
        } finally {
            model.writeUnlock(index);
        }
    }
}
