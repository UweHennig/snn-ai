/**
 * @(#)SomaModelMangager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.SomaModel;

/**
 * SomaModelMangager
 *
 * @author Uwe Hennig
 */
public class SomaModelMangager {
    private static SomaModelMangager INSTANCE;

    private SomaModel model;
    private int       nextOffset = 0;

    private SomaModelMangager(int capacity) {
        this.model = new SomaModel(capacity);
    }

    public static SomaModelMangager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SomaModelMangager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static SomaModelMangager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap soma memory");
        }
        return nextOffset++;
    }

    public SomaModel getModel() {
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
