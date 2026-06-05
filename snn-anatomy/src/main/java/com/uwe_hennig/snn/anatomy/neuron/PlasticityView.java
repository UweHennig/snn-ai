/**
 * @(#)PlasticityView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * PlasticityView
 *
 * @author Uwe Hennig
 */
public class PlasticityView {
    private final PlasticityModel model;
    private final long            index;

    public PlasticityView(int index, PlasticityModel model) {
        assert model != null : "Model must not be null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
    }

    public PlasticityModel getModel() {
        return model;
    }

    public long getViewId() {
        return index;
    }
}
