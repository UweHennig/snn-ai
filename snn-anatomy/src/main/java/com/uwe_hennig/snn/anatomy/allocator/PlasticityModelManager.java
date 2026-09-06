/**
 * @(#)PlasticityModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.PlasticityModel;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityView;

/**
 * PlasticityModelManager
 *
 * @author Uwe Hennig
 */
public class PlasticityModelManager {
    private static PlasticityModelManager INSTANCE;

    private PlasticityModel model;
    private int             nextOffset = 0;

    private PlasticityModelManager(int capacity) {
        this.model = new PlasticityModel(capacity);
    }

    public static PlasticityModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PlasticityModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static PlasticityModelManager instance() {
        return INSTANCE;
    }

    public PlasticityView createView() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap plasticity memory");
        }
        PlasticityView view = new PlasticityView(model, nextOffset++);
        return view;
    }

    public int capacity() {
        return nextOffset;
    }

    public PlasticityModel getModel() {
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
