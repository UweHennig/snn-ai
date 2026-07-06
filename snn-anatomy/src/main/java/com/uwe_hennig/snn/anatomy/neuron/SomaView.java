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
    private final PotentialView  potentialView;
    private final ThresholdView  thresholdView;
    private final PlasticityView stpView;
    private final PlasticityView ltpView;

    public SomaView(int index, SomaModel model, PotentialView potentialView, ThresholdView thresholdView, PlasticityView stpView, PlasticityView ltpView) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.potentialView = potentialView;
        this.thresholdView = thresholdView;
        this.stpView = stpView;
        this.ltpView = ltpView;
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

            model.setPotentialId(index, potentialView.getViewId());
            model.setThresholdId(index, thresholdView.getViewId());
            model.setStpId(index, stpView.getViewId());
            model.setLtpId(index, ltpView.getViewId());
        } finally {
            model.writeUnlock(index);
        }
    }
}
