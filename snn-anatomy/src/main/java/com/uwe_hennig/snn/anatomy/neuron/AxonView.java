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
    private final AxonModel model;
    private final int index;

    public AxonView(AxonModel model, int index) {
        this.model = model;
        this.index = index;
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

    // ----- Getter/Setter -----

    public int getSynapseRef() {
        return model.getSynapseRef(index);
    }

    public int getNeuronId() {
        return model.getNeuronId(index);
    }

    public int getModulatorId() {
        return model.getModulatorId(index);
    }

    public int getId() {
        return index;
    }

    public void setStructure(int fieldId, int neuronId, int synapseRef) {
        model.writeLock(index);
        try {
            // TODO Modulator-Id
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setSynapseRef(index, synapseRef);
        } finally {
            model.writeUnlock(index);
        }
    }
}
