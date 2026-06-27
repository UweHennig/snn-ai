/**
 * @(#)FieldView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.core.FieldNode;

/**
 * FieldView
 * Field is currently a container for neuron fields and neurons.
 * TODO check whether the class is being used. See FieldNode
 * @author Uwe Hennig
 */
public final class FieldView {
    private final FieldModel model;
    private final int        index;
    private final FieldNode  node;

    public FieldView(int index, FieldModel model, FieldNode node) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.node  = node;
    }

    public FieldModel getModel() {
        return model;
    }

    public int getViewId() {
        return index;
    }

    public int type() {
        return model.getType(index);
    }

    public void setType(int type) {
        try {
            model.writeLock(index);
            model.setType(index, type);
        } finally {
            model.writeUnlock(index);
        }
    }

    public int level() {
        return model.getLevel(index);
    }

    public void setLevel(int level) {
        try {
            model.writeLock(index);
            model.setLevel(index, level);
        } finally {
            model.writeUnlock(index);
        }
    }

    // TODO addParentIds, addChildIds, addNeuronIds
    // TODO getParentIds, getChildIds, getNeuronIds

}
