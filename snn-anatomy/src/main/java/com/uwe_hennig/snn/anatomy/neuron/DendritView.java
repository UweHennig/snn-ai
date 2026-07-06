/**
 * @(#)DendritView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * DendritView
 * Unlike conventional SNNs, the Dendrit network handles the weights
 *
 * @author Uwe Hennig
 */
public final class DendritView {
    private final int          index;
    private final DendritModel model;

    private WeightView weightView;

    public DendritView(int index, DendritModel model, WeightView weightView) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.weightView = weightView;
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

    public DendritModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    public WeightView getWeightView() {
        return weightView;
    }

    public int getFieldId() {
        return model.getFiedlId(index);
    }

    public int getNeuronId() {
        return model.getNeuronId(index);
    }

    public int getSomaId() {
        return model.getSomaId(index);
    }

    public void setStructure(int fieldId, int neuronId, int somaId) {
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setSomaId(index, somaId);
            model.setWeightId(index, weightView.getViewId());
        } finally {
            model.writeUnlock(index);
        }
    }
}
