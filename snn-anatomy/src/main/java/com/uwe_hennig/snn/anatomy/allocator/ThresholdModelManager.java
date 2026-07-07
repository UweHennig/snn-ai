/**
 * @(#)ThresholdModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.ThresholdModel;

/**
 * ThresholdModelManager
 *
 * @author Uwe Hennig
 */
public class ThresholdModelManager {
    private static ThresholdModelManager INSTANCE;
    private ThresholdModel              model;
    private int                         nextOffset = 0;

    private ThresholdModelManager(int capacity) {
        this.model = new ThresholdModel(capacity);
    }

    public static ThresholdModelManager init(int capacity) {
        INSTANCE = new ThresholdModelManager(capacity);
        return INSTANCE;
    }

    public static ThresholdModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public ThresholdModel getModel() {
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
