/**
 * @(#)AxonAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.AxonModel;

/**
 * AxonAllocator
 *
 * @author Uwe Hennig
 */
public class AxonModelManager {
    private static AxonModelManager INSTANCE;

    private AxonModel model;
    private int       nextOffset = 0;

    private AxonModelManager(int capacity) {
        this.model = new AxonModel(capacity);
    }

    public static AxonModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (AxonModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AxonModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static AxonModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap axon memory");
        }
        return nextOffset++;
    }

    public AxonModel getModel() {
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
