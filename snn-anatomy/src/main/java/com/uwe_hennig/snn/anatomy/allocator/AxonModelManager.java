/**
 * @(#)AxonAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.AxonModel;

/**
 * AxonAllocator
 *
 * @author Uwe Hennig
 */
public class AxonModelManager {
    private static AxonModelManager INSTANCE;
    private AxonModel               model;
    private int                     nextOffset = 0;

    private AxonModelManager(int capacity) {
        this.model = new AxonModel(capacity);
    }

    public static AxonModelManager init(int capacity) {
        INSTANCE = new AxonModelManager(capacity);
        return INSTANCE;
    }

    public static AxonModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public AxonModel getModel() {
        return model;
    }

    public void close() {
        nextOffset = 0;
        model.close();
        model = null;
        INSTANCE = null;
    }
}
