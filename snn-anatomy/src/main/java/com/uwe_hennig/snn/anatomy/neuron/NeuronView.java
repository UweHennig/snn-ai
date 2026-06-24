/**
 * @(#)NeuronView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * NeuronView
 * @author Uwe Hennig
 */
public final class NeuronView {
    private final int         index;
    private final NeuronModel model;

    private final int fieldId;
    private int       neuronElementRef;

    public NeuronView(int index, NeuronModel model, int fieldId) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.fieldId = fieldId;
        this.neuronElementRef = -1;

        initData();
    }

    private void initData() {
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronElementRef(index, neuronElementRef);
        } finally {
            model.writeUnlock(index);
        }
    }

    public NeuronModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    public int getFieldId() {
        return fieldId;
    }

}
