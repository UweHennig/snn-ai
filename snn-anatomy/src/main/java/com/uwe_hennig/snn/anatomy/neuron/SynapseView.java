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
    private final SynapseModel model;
    private final int          index;

    public SynapseView(SynapseModel model, int index) {
        this.model = model;
        this.index = index;
    }

    public int getId() {
        return index;
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

    // ----- getter/setter -----

    public void setTargetRef(int targetId) {
        model.writeLock(index);
        model.setTargetId(index, targetId);
        model.writeUnlock(index);
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

    public void setStructure(int fieldId, int neuronId, int modulatorId) {
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setModulatorId(index, modulatorId);
        } finally {
            model.writeUnlock(index);
        }
    }
}
