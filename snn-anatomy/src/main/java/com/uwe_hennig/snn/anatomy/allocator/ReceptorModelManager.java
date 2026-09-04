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
    private MatrixModel          model;

    private int nextIndex = 0;

    private ReceptorModelManager(int numReceptors, long totalSize) {
        receptors = new ReceptorView[numReceptors];
        model = new MatrixModel(numReceptors, totalSize);
    }

    public static ReceptorModelManager init(int numReceptors, long totalSize) {
        if (INSTANCE == null) {
            synchronized (ReceptorModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ReceptorModelManager(numReceptors, totalSize);
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

        nextIndex = model.registerMatrix(numHeaders, numRows, numColumns, numSlotsPerCell);
        ReceptorView view = new ReceptorView(model, nextIndex);
        receptors[nextIndex] = view;

        return nextIndex;
    }

    public static long matrixSize(int numHeaders, int numRows, int numColumns, int numSlotsPerCell) {
        return MatrixModel.matrixSize(numHeaders, numRows, numColumns, numSlotsPerCell);
    }

    public static long metaSize() {
        return MatrixModel.metaSize();
    }

    public ReceptorView getRecptorView(int receptorId) {
        return receptors[receptorId];
    }

    public int getNumReceptors() {
        return receptors.length;
    }

    public void close() {
        if (INSTANCE != null) {
            if (model != null) {
                model.close();
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
