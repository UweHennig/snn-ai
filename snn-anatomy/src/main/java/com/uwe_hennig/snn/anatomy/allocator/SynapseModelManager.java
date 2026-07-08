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
    private SynapseModel               model;
    private int                        nextOffset = 0;

    private SynapseModelManager(int capacity) {
        model = new SynapseModel(capacity);
    }

    public static SynapseModelManager init(int capacity) {
        INSTANCE = new SynapseModelManager(capacity);
        return INSTANCE;
    }

    public static SynapseModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public SynapseModel getModel() {
        return model;
    }

    public void close() {
        nextOffset = 0;
        model.close();
        model = null;
        INSTANCE = null;
    }
}
