/**
 * @(#)ReceptorModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.peripheral.ReceptorView;
import com.uwe_hennig.snn.util.MatrixModel;

/**
 * ReceptorModelManager
 *
 * @author Uwe Hennig
 */
public final class ReceptorModelManager {
    private static ReceptorModelManager INSTANCE;

    private final ReceptorView[] receptors;

    private int nextIndex    = 0;

    private ReceptorModelManager(int numReceptors) {
        receptors = new ReceptorView[numReceptors];
    }

    public static ReceptorModelManager init(int numReceptors) {
        if (INSTANCE == null) {
            synchronized (ReceptorModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReceptorModelManager(numReceptors);
                }
            }
        }
        return INSTANCE;
    }

    public static ReceptorModelManager instance() {
        return INSTANCE;
    }

    // return receptor id
    public int newReceptor(int capacity, int numHeaders, int numRows, int numColumns, int numSlotsPerCell) {
        if (receptors.length <= nextIndex) {
            throw new IllegalStateException("Out of off receptors memory");
        }
        MatrixModel model = new MatrixModel(capacity, numHeaders, numRows, numColumns, numSlotsPerCell);
        ReceptorView view = new ReceptorView(model);
        receptors[nextIndex] = view;

        return nextIndex++;
    }

    public ReceptorView getRecptorView(int receptorId) {
        return receptors[receptorId];
    }

    public int getNumReceptors() {
        return receptors.length;
    }

    public void close() {
        if (INSTANCE != null) {
            for (int i = 0; i < receptors.length; i++) {
                ReceptorView view = receptors[i];
                if (view != null) {
                    view.getModel().close();
                }
            }
        }
    }

    public void save(String folder) {
        /* TODO: Save model */
    }

    public void load(String folder) {
        /* TODO Load model */
    }

}
