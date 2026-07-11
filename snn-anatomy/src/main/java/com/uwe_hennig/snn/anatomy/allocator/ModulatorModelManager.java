/**
 * @(#)ModulatorModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.ModulatorModel;

/**
 * ModulatorModelManager
 *
 * @author Uwe Hennig
 */
public class ModulatorModelManager {
    private static ModulatorModelManager INSTANCE;

    private ModulatorModel model;
    private int            nextOffset = 0;

    private ModulatorModelManager(int capacity) {
        this.model = new ModulatorModel(capacity);
    }

    public static ModulatorModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ModulatorModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static ModulatorModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap modulator memory");
        }
        return nextOffset++;
    }

    public int capacity() {
        return nextOffset;
    }

    public ModulatorModel getModel() {
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
