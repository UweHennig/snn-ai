/**
 * @(#)SynapseModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.SynapseModel;

/**
 * SynapseModelManager
 *
 * @author Uwe Hennig
 */
public class SynapseModelManager {
    private static SynapseModelManager INSTANCE;

    private SynapseModel model;
    private int          nextOffset = 0;

    private SynapseModelManager(int capacity) {
        model = new SynapseModel(capacity);
    }

    public static SynapseModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (SynapseModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SynapseModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static SynapseModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap synapse memory");
        }
        return nextOffset++;
    }

    public int capacity() {
        return nextOffset;
    }

    public SynapseModel getModel() {
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
