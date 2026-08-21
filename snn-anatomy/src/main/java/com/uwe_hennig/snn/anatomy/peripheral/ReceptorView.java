/**
 * @(#)ReceptorView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import com.uwe_hennig.snn.anatomy.allocator.ReceptorModelManager;

/**
 * ReceptorView
 *
 * @author Uwe Hennig
 */
public class ReceptorView {
    // ----- getter/setter -----

    public static int getTemporalFilterIndex(int index) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        return model.getTemporalFilterIndex(index);
    }

    public static void setTemporalFilterIndex(int index, int value) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.setTemporalFilterIndex(index, value);
    }

    public static int getInformationFilterIndex(int index) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        return model.getInformationFilterIndex(index);
    }

    public static void setInformationFilterIndex(int index, int value) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.setInformationFilterIndex(index, value);
    }

    public static int getDendritId(int index, int row, int col) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        return model.getDendriteId(index, row, col);
    }

    public static void setDendriteId(int index, int row, int col, int id) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.setDendriteId(index, row, col, id);
    }

    // ----- lock/unlock -----

    public static void readLock(int index) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.readLock(index);
    }

    public static void readUnlock(int index) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.readUnlock(index);
    }

    public static void writeLock(int index) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.writeLock(index);
    }

    public static void writeUnlock(int index) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.writeUnlock(index);
    }

}
