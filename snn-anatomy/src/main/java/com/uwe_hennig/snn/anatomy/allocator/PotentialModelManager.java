/**
 * @(#)PotentialModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.PotentialModel;

/**
 * PotentialModelManager
 *
 * @author Uwe Hennig
 */
public class PotentialModelManager {
    private static PotentialModelManager INSTANCE;
    private PotentialModel               model;
    private int                          nextOffset = 0;

    private PotentialModelManager(int capacity) {
        this.model = new PotentialModel(capacity);
    }

    public static PotentialModelManager init(int capacity) {
        INSTANCE = new PotentialModelManager(capacity);
        return INSTANCE;
    }

    public static PotentialModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public PotentialModel getModel() {
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
