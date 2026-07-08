/**
 * @(#)DendritAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.DendritModel;

/**
 * DendritModelManager
 *
 * @author Uwe Hennig
 */
public class DendritModelManager {
    private static DendritModelManager INSTANCE;
    private DendritModel model;
    private int nextOffset = 0;

    private DendritModelManager(int capacity) {
        model = new DendritModel(capacity);
    }

    public static DendritModelManager init(int capacity) {
        INSTANCE = new DendritModelManager(capacity);
        return INSTANCE;
    }

    public static DendritModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public DendritModel getModel() {
        return model;
    }

    public void close() {
        nextOffset = 0;
        model.close();
        model = null;
        INSTANCE = null;
    }
}
