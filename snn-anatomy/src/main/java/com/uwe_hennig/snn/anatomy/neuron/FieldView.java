/**
 * @(#)FieldView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.core.FieldGraph;

/**
 * FieldView Field is currently a container for neuron fields and neurons.
 *
 * @author Uwe Hennig
 */
public class FieldView {
    private final FieldModel model;
    private final int        index;

    private int parentsRef  = -1;
    private int childrenRef = -1;
    private int neuronsRef  = -1;

    public FieldView(int index, FieldModel model) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
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
            model.lock(index);
            model.setType(index, type);
        } finally {
            model.unlock(index);
        }
    }

    public int level() {
        return model.getLevel(index);
    }

    public void setLevel(int level) {
        try {
            model.lock(index);
            model.setLevel(index, level);
        } finally {
            model.unlock(index);
        }
    }

    public void addParentFields(int[] parentIds) {
        assert FieldGraph.get() != null : "FieldGraph not created!";
        parentsRef = FieldGraph.get().addParentFieldIds(parentIds);
        model.setParentsRef(index, parentsRef);
    }

    public int[] getParentFields() {
        assert FieldGraph.get() != null : "FieldGraph not created!";
        return FieldGraph.get().getParentFieldIds(parentsRef);
    }

    public void addChildFields(int[] childIds) {
        assert FieldGraph.get() != null : "FieldGraph not created!";
        childrenRef = FieldGraph.get().addChildFieldIds(childIds);
        model.setChildrenRef(index, childrenRef);
    }

    public int[] getChildIds() {
        assert FieldGraph.get() != null : "FieldGraph not created!";
        return FieldGraph.get().getChildFieldIds(childrenRef);
    }

    public void addNeuronIds(int[] neuronIds) {
        assert FieldGraph.get() != null : "FieldGraph not created!";
        neuronsRef = FieldGraph.get().addNeuronIds(neuronIds);
        model.setNeuronsRef(index, neuronsRef);
    }

    public int[] getNeuronIds() {
        assert FieldGraph.get() != null : "FieldGraph not created!";
        return FieldGraph.get().getNeuronIds(neuronsRef);
    }
}
