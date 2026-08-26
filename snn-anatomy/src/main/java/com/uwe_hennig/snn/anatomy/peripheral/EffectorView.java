/**
 * @(#)EffectorView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import com.uwe_hennig.snn.anatomy.allocator.EffectorModelManager;

/**
 * EffectorView
 *
 * @author Uwe Hennig
 */
public class EffectorView {
    // ----- getter/setter -----

    public static float getTimeWindow(int index) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        return model.getTimeWindow(index);
    }

    public static void setTimeWindow(int index, int value) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        model.setTimeWindow(index, value);
    }

    public static void setValue(int index, int row, int column, float value) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        model.setValue(index, row, column, value);
    }

    public static float getValue(int index, int row, int column) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        return model.getValue(index, row, column);
    }

    // ----- lock/unlock -----

    public static void readLock(int index) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        model.readLock(index);
    }

    public static void readUnlock(int index) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        model.readUnlock(index);
    }

    public static void writeLock(int index) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        model.writeLock(index);
    }

    public static void writeUnlock(int index) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        model.writeUnlock(index);
    }
}
