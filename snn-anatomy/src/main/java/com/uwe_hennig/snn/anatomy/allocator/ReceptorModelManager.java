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

    private ReceptorModelManager(int rowDendrites, int colDendrites) {
        this.model = new ReceptorModel(rowDendrites, colDendrites);
    }

    public static ReceptorModelManager init(int rowDendrites, int colDendrites) {
        if (INSTANCE == null) {
            synchronized (ReceptorModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReceptorModelManager(rowDendrites, colDendrites);
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
