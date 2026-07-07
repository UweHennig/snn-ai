/**
 * @(#)PotentialAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.PotentialModel;
import com.uwe_hennig.snn.anatomy.neuron.PotentialView;

/**
 * PotentialAllocator
 *
 * @author Uwe Hennig
 */
public class PotentialAllocator {
    private static PotentialAllocator INSTANCE;

    private final PotentialModel model;

    private int nextOffset = 0;

    private PotentialAllocator(int capacity) {
        this.model = new PotentialModel(capacity);
    }

    public static PotentialAllocator initInstance(int capacity) {
        INSTANCE = new PotentialAllocator(capacity);
        return INSTANCE;
    }

    public static PotentialAllocator instance() {
        return INSTANCE;
    }

    public PotentialView newPotentialView() {
        if (nextOffset >= model.getCapacity()) {
            throw new IllegalStateException("Out of Offheap memory");
        }
        int offset = nextOffset++;
        PotentialView potentialView = new PotentialView(offset, model);
        return potentialView;
    }
}
