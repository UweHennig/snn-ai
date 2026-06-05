/**
 * @(#)NeuronView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * NeuronView
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class NeuronView {
    private final long index;
    private final NeuronModel model;

    private final long fieldId;
    private long neuronElementRef;


    public NeuronView(long index, NeuronModel model, long fieldId) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.fieldId = fieldId;
        this.neuronElementRef = -1;

        initData();
    }

    private void initData() {
        model.lock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronElementRef(index, neuronElementRef);
        } finally {
            model.unlock(index);
        }
    }

    public NeuronModel getModel() {
        return model;
    }

    public long getViewId() {
        return index;
    }

    public long getFieldId() {
        return fieldId;
    }

}
