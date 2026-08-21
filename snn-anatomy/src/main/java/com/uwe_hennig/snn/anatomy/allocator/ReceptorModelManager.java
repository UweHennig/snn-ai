/**
 * @(#)ReceptorModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.peripheral.ReceptorModel;

/**
 * ReceptorModelManager
 *
 * @author Uwe Hennig
 */
public class ReceptorModelManager {
    private static ReceptorModelManager INSTANCE;

    private ReceptorModel model;
    private int capacity;

    private ReceptorModelManager(int capacity, int rows, int columns) {
        this.model = new ReceptorModel(capacity, rows, columns);
        this.capacity = capacity;
    }

    public static ReceptorModelManager init(int capacity, int rows, int columns) {
        if (INSTANCE == null) {
            synchronized (ReceptorModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReceptorModelManager(capacity, rows, columns);
                }
            }
        }
        return INSTANCE;
    }

    public static ReceptorModelManager instance() {
        return INSTANCE;
    }

    public ReceptorModel getModel() {
        return model;
    }

    public int getCapacity() {
        return capacity;
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
