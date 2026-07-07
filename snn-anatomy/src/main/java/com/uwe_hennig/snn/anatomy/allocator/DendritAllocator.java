/**
 * @(#)DendritAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.DendritModel;
import com.uwe_hennig.snn.anatomy.neuron.DendritView;

/**
 * DendritAllocator
 *
 * @author Uwe Hennig
 */
public class DendritAllocator {
    private static DendritAllocator INSTANCE;

    private final DendritModel model;

    private int nextOffset = 0;

    private DendritAllocator(int capacity) {
        model = new DendritModel(capacity);
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

        DendritView view = new DendritView(offset, model);
        view.setStructure(fieldId, neuronId, somaId);

        return view;
    }

    public DendritView viewAt(int viewId) {
        if (viewId >= nextOffset) {
            return null;
        }

        return new DendritView(viewId, model);
    }

    public void close() {
        model.close();
    }
}
