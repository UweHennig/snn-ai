/**
 * @(#)SomaView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * SomaView
 *
 * @author Uwe Hennig
 */
public final class SomaView {
    private final int            index;
    private final SomaModel      model;

    public SomaView(int index, SomaModel model) {
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

    // ----- Getter/Setter -----

    public SomaModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    public int getFieldId() {
        return model.getFieldId(index);
    }

    public int getNeuronId() {
        return model.getNeuronId(index);
    }

    public int getAxonId() {
        return model.getAxonId(index);
    }

    public void setStructure(int fieldId, int neuronId, int axonId) {
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setAxonId(index, axonId);
        } finally {
            model.writeUnlock(index);
        }
    }
}
