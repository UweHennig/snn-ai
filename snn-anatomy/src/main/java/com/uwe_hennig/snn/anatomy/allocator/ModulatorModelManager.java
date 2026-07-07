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
    private ModulatorModel               model;
    private int                          nextOffset = 0;

    private ModulatorModelManager(int capacity) {
        this.model = new ModulatorModel(capacity);
    }

    public static ModulatorModelManager init(int capacity) {
        INSTANCE = new ModulatorModelManager(capacity);
        return INSTANCE;
    }

    public static ModulatorModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public ModulatorModel getModel() {
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
