/**
 * @(#)PlasticityModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.PlasticityModel;

/**
 * PlasticityModelManager
 *
 * @author Uwe Hennig
 */
public class PlasticityModelManager {
    private static PlasticityModelManager INSTANCE;
    private PlasticityModel               model;
    private int                           nextOffset = 0;

    private PlasticityModelManager(int capoacity) {
        this.model = new PlasticityModel(capoacity);
    }

    public static PlasticityModelManager init(int capacity) {
        INSTANCE = new PlasticityModelManager(capacity);
        return INSTANCE;
    }

    public static PlasticityModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public PlasticityModel getModel() {
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
