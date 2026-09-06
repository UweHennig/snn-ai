/**
 * @(#)ThresholdModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.ThresholdModel;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdView;

/**
 * ThresholdModelManager
 *
 * @author Uwe Hennig
 */
public class ThresholdModelManager {
    private static ThresholdModelManager INSTANCE;

    private ThresholdModel model;
    private int            nextOffset = 0;

    private ThresholdModelManager(int capacity) {
        this.model = new ThresholdModel(capacity);
    }

    public static ThresholdModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThresholdModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static ThresholdModelManager instance() {
        return INSTANCE;
    }

    public ThresholdView createView() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap threshold memory");
        }
        return new ThresholdView(model, nextOffset++);
    }

    public int capacity() {
        return nextOffset;
    }

    public ThresholdModel getModel() {
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
