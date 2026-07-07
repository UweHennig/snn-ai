/**
 * @(#)WeightModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.WeightModel;

/**
 * WeightModelManager
 *
 * @author Uwe Hennig
 */
public class WeightModelManager {
    private static WeightModelManager INSTANCE;
    private final WeightModel         model;
    private int                       nextOffset = 0;

    private WeightModelManager(int capacity) {
        this.model = new WeightModel(capacity);
    }

    public static WeightModelManager init(int capacity) {
        INSTANCE = new WeightModelManager(capacity);
        return INSTANCE;
    }

    public static WeightModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public WeightModel getModel() {
        return model;
    }

    public void close() {
        model.close();
    }

    public void save(String folder) {
        /* Speichere model in folder/weights.bin */
    }

    public void load(String folder) {
        /* Lade model aus folder/weights.bin */
    }
}