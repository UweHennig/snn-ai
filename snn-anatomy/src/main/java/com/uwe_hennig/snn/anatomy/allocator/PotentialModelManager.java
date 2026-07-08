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

    private PotentialModel model;
    private int            nextOffset = 0;

    private PotentialModelManager(int capacity) {
        this.model = new PotentialModel(capacity);
    }

    public static PotentialModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PotentialModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static PotentialModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() >= nextOffset) {
            throw new IllegalStateException("Out of Offheap memory");
        }
        return nextOffset++;
    }

    public PotentialModel getModel() {
        return model;
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.nextOffset = 0;
            INSTANCE.model.close();
            INSTANCE.model = null;
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
