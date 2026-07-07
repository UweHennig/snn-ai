/**
 * @(#)SynapseAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.SynapseModel;
import com.uwe_hennig.snn.anatomy.neuron.SynapseView;

/**
 * SynapseAllocator
 *
 * @author Uwe Hennig
 */
public class SynapseAllocator {
    private static SynapseAllocator INSTANCE;

    private final SynapseModel model;

    private int nextOffset = 0;

    private SynapseAllocator(int capacity) {
        model = new SynapseModel(capacity);
    }

    public static SynapseAllocator initInstance(int capacity) {
        INSTANCE = new SynapseAllocator(capacity);
        return INSTANCE;
    }

    public static SynapseAllocator instance() {
        return INSTANCE;
    }

    public SynapseView newSynapseView(int fieldId, int neuronId) {
        if (nextOffset >= model.getCapacity()) {
            throw new IllegalStateException("Out of Offheap memory");
        }

        int offset = nextOffset++;

        SynapseView view = new SynapseView(offset, model);

        view.setStructure(fieldId, neuronId, -1);
        return view;
    }

    public SynapseView viewAt(int viewId) {
        if (viewId >= nextOffset) {
            return null;
        }

        SynapseView view = new SynapseView(viewId, model);
        return view;
    }

    public void close() {
        model.close();
    }
}
