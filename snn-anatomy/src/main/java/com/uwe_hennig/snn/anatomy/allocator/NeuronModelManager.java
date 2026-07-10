/**
 * @(#)NeuronModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.NeuronModel;

/**
 * NeuronModelManager
 *
 * @author Uwe Hennig
 */
public class NeuronModelManager {
    public static NeuronModelManager INSTANCE;

    private NeuronModel model;
    private int         nextOffset = 0;

    private NeuronModelManager(int capacity) {
        this.model = new NeuronModel(capacity);
    }

    public static NeuronModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static NeuronModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap NeuronModel memory");
        }
        return nextOffset++;
    }

    public NeuronModel getModel() {
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
