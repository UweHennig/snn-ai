/**
 * @(#)NeuronFieldAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;

/**
 * NeuronFieldAllocator
 *
 * @author Uwe Hennig
 */
public class NeuronFieldModelManager {
    private static NeuronFieldModelManager INSTANCE;

    private NeuronFieldModel model;
    private int              nextOffset = 0;

    private NeuronFieldModelManager(int capacity) {
        this.model = new NeuronFieldModel(capacity);
    }

    public static NeuronFieldModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronFieldModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static NeuronFieldModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap NeuronField memory");
        }
        return nextOffset++;
    }

    public NeuronFieldModel getModel() {
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
