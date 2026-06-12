/**
 * @(#)SomaView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * SomaView
 * @author Uwe Hennig
 */
public final class SomaView {
    private final int       index;
    private final SomaModel model;

    private final int fieldId;
    private final int neuronId;
    private final int potentialId;
    private final int thresholdId;
    private final int stpId;
    private final int ltpId;

    public SomaView(int index, SomaModel model, int fieldId, int neuronId, int potentialId, int thresholdId, int stpId, int ltpId) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.fieldId = fieldId;
        this.neuronId = neuronId;

        this.potentialId = potentialId;
        this.thresholdId = thresholdId;
        this.stpId = stpId;
        this.ltpId = ltpId;

        initData();
    }

    private void initData() {
        try {
            model.lock(index);
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setPotentialId(index, potentialId);
            model.setThresholdId(index, thresholdId);
            model.setStpId(index, stpId);
            model.setLtpId(index, ltpId);
        } finally {
            model.unlock(index);
        }
    }

}
