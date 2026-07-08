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
    private int          nextOffset = 0;

    private DendritModelManager(int capacity) {
        model = new DendritModel(capacity);
    }

    public static DendritModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DendritModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static DendritModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() >= nextOffset) {
            throw new IllegalStateException("Out of Offheap memory");
        }
        return nextOffset++;
    }

    public DendritModel getModel() {
        return model;
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.model.close();
            INSTANCE.model = null;
            INSTANCE.nextOffset = 0;
            INSTANCE = null;
        }
    }

    public void save(String folder) {
        /* TODO: Save model */
    }

    public void load(String folder) {
        /* TODO Load model */
    }
}
