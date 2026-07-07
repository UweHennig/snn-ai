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
    private EdgeModel               model;
    private int                     nextOffset = 0;

    private EdgeModelManager(int capacity) {
        this.model = new EdgeModel(capacity);
    }

    public static EdgeModelManager init(int capacity) {
        INSTANCE = new EdgeModelManager(capacity);
        return INSTANCE;
    }

    public static EdgeModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public EdgeModel getModel() {
        return model;
    }

    public void close() {
        nextOffset = 0;
        model.close();
        model = null;
        INSTANCE = null;
    }

    public void save(String folder) {
        /* Speichere model in folder/weights.bin */
    }

    public void load(String folder) {
        /* Lade model aus folder/weights.bin */
    }
}
