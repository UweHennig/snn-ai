/**
 * @(#)SomaView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * SomaView
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class SomaView {
    private final long index;
    private final SomaModel model;

    private final long fieldId;
    private final long neuronId;
    private final long potentialId;
    private final long thresholdId;
    private final long stpId;
    private final long ltpId;

    public SomaView(long index, SomaModel model, long fieldId, long neuronId, long potentialId, long thresholdId, long stpId, long ltpId) {
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
