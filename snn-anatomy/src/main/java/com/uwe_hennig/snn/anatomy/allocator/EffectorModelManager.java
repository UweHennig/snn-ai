/**
 * @(#)EffectorModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.peripheral.EffectorModel;

/**
 * EffectorModelManager
 *
 * @author Uwe Hennig
 */
public class EffectorModelManager {
    private static EffectorModelManager INSTANCE;

    private EffectorModel model;

    private EffectorModelManager(int capacity, int rows, int columns) {
        this.model = new EffectorModel(capacity, rows, columns);
    }

    public static EffectorModelManager init(int capacity, int rows, int columns) {
        if (INSTANCE == null) {
            synchronized (ReceptorModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new EffectorModelManager(capacity, rows, columns);
                }
            }
        }
        return INSTANCE;
    }

    public static EffectorModelManager instance() {
        return INSTANCE;
    }

    public EffectorModel getModel() {
        return model;
    }

    public int getCapacity() {
        return model.getCapacity();
    }

    public static void close() {
        if (INSTANCE != null) {
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
