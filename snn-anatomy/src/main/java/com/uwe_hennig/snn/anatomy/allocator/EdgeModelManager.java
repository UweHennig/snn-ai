/**
 * @(#)EdgeModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.EdgeModel;

/**
 * EdgeModelManager
 *
 * @author Uwe Hennig
 */
public class EdgeModelManager {
    private static EdgeModelManager INSTANCE;

    private EdgeModel model;
    private int       nextOffset = 0;

    private EdgeModelManager(int capacity) {
        this.model = new EdgeModel(capacity);
    }

    public static EdgeModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EdgeModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static EdgeModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() >= nextOffset) {
            throw new IllegalStateException("Out of Offheap memory");
        }
        return nextOffset++;
    }

    public EdgeModel getModel() {
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
