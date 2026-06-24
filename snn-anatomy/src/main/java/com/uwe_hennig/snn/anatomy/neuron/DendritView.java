/**
 * @(#)DendritView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * DendritView Unlike conventional SNNs, the Dendrit network handles the weights
 *
 * @author Uwe Hennig
 */
public final class DendritView {
    private final int          index;
    private final DendritModel model;

    private WeightView weightView;
    private int        fieldId;
    private int        neuronId;
    private int        somaId;

    public DendritView(int index, DendritModel model, WeightView weightView, int fieldId, int neuronId, int somaId) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.fieldId = fieldId;
        this.neuronId = neuronId;
        this.somaId = somaId;
        this.weightView = weightView;

        initData();
    }

    // TODO remove
    private void initData() {
        try {
            model.writeLock(index);
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setSomaId(index, somaId);
            model.setWeightId(index, weightView.getViewId());
        } finally {
            model.writeUnlock(index);
        }
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
        return fieldId;
    }

    public int getNeuronId() {
        return neuronId;
    }

    public int getSomaId() {
        return somaId;
    }

}
