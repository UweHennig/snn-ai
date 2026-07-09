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

    private WeightModel model;
    private int         nextOffset = 0;

    private WeightModelManager(int capacity) {
        this.model = new WeightModel(capacity);
    }

    public static WeightModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WeightModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static WeightModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap weight memory");
        }
        return nextOffset++;
    }

    public WeightModel getModel() {
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