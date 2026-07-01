/**
 * @(#)DendritAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.DendritModel;
import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.WeightModel;
import com.uwe_hennig.snn.anatomy.neuron.WeightView;

/**
 * DendritAllocator
 *
 * @author Uwe Hennig
 */
public class DendritAllocator {
    private static DendritAllocator INSTANCE;

    private final DendritModel model;
    private final WeightModel  weightModel;

    private int nextOffset = 0;

    private DendritAllocator(int capacity) {
        model = new DendritModel(capacity);
        weightModel = new WeightModel(capacity);
    }

    public static DendritAllocator initInstance(int capacity) {
        INSTANCE = new DendritAllocator(capacity);
        return INSTANCE;
    }

    public static DendritAllocator instance() {
        return INSTANCE;
    }

    public DendritView newDendritView(int fieldId, int neuronId, int somaId) {
        if (nextOffset >= model.getCapacity()) {
            throw new IllegalStateException("Out of Offheap memory");
        }

        int offset = nextOffset++;

        WeightView weightView = new WeightView(offset, weightModel);
        DendritView view = new DendritView(offset, model, weightView);
        view.setStructure(fieldId, neuronId, somaId);

        return view;
    }

    public DendritView viewAt(int viewId) {
        if (viewId >= nextOffset) {
            return null;
        }
        WeightView weightView = new WeightView(viewId, weightModel);
        return new DendritView(viewId, model, weightView);
    }
}
