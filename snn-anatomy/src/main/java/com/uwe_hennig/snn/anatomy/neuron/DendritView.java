/**
 * @(#)DendritView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * DendritView
 * Unlike conventional SNNs, the Dendrit network handles the weights
 * @author Uwe Hennig
 */
public class DendritView {
    private final long index;
    private final DendritModel model;

    private WeightView weightView;
    private long fieldId;
    private long neuronId;
    private long somaId;

    public DendritView(long index, DendritModel model, WeightView weightView, long fieldId, long neuronId, long somaId) {
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

    private void initData() {
        try {
            model.lock(index);
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setSomaId(index, somaId);
            model.setWeightId(index, weightView.getViewId());
        } finally {
            model.unlock(index);
        }
    }

    public DendritModel getModel() {
        return model;
    }

    public long getViewId() {
        return index;
    }

    public WeightView getWeightView() {
        return weightView;
    }

    public long getFieldId() {
        return fieldId;
    }

    public long getNeuronId() {
        return neuronId;
    }

    public long getSomaId() {
        return somaId;
    }

}
