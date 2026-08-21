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

    public static int getTemporalFilterIndex(int index) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        return model.getTemporalFilterIndex(index);
    }

    public static void setTemporalFilterIndex(int index, int value) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        model.setTemporalFilterIndex(index, value);
    }

    public static int getRelatedElement(int index, int position) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        return model.getRelatedId(index, position);
    }

    public static void setRelatedElement(int index, int position, int value) {
        EffectorModel model = EffectorModelManager.instance().getModel();
        model.setRelatedId(index, position, value);
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
