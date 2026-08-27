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

    public static float getIntakeDistance(int index) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        return model.getIntakeDistance(index);
    }

    public static void setIntakeDistance(int index, float value) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.setIntakeDistance(index, value);
    }

    public static int getTargetId(int index, int row, int col) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        return model.getTargetId(index, row, col);
    }

    public static void setTargetId(int index, int row, int col, int id) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.setTargetId(index, row, col, id);
    }

    public static int getTargetType(int index, int row, int col) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        return model.getTargetType(index, row, col);
    }

    public static void setTargetType(int index, int row, int col, int id) {
        ReceptorModel model = ReceptorModelManager.instance().getModel();
        model.setTargetType(index, row, col, id);
    }
}
