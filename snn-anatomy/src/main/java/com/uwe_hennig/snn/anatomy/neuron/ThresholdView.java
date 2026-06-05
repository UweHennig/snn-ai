/**
 * @(#)ThresholdView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * ThresholdView
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class ThresholdView {
    private final ThresholdModel model;
    private final long        index;

    public ThresholdView(int index, ThresholdModel model) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;

    }

    public ThresholdModel getModel() {
        return model;
    }

    public long getViewId() {
        return index;
    }

}
