/**
 * @(#)SomaAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.SomaModel;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;

/**
 * SomaAllocator
 *
 * @author Uwe Hennig
 */
public class SomaAllocator {
    private static SomaAllocator INSTANCE;

    private final SomaModel model;
    private int nextOffset = 0;

    private SomaAllocator(int capacity) {
        this.model = new SomaModel(capacity);
    }

    public static SomaAllocator initInstance(int capacity) {
        INSTANCE = new SomaAllocator(capacity);
        return INSTANCE;
    }

    public static SomaAllocator instance() {
        return INSTANCE;
    }

    public SomaView newSomaView(int fieldId, int neuronId, int axonId) {
        if (nextOffset >= model.getCapacity()) {
            throw new IllegalStateException("Out of Offheap memory");
        }

        int offset = nextOffset++;

        SomaView somaView = createViews(offset);
        somaView.setStructure(fieldId, neuronId, axonId);

        return somaView;
    }

    public SomaView viewAt(int viewId) {
        if (viewId >= nextOffset) {
            return null;
        }
        return createViews(viewId);
    }

    private SomaView createViews(int viewId) {
        if (viewId >= model.getCapacity()) {
            throw new IllegalStateException("Out of Offheap memory");
        }

        SomaView somaView = new SomaView(viewId, model);
        return somaView;
    }

    public void close() {
        model.close();
    }
}
