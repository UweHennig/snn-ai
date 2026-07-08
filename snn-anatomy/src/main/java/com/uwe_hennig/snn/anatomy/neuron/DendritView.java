/**
 * @(#)DendritView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.DendritModelManager;

/**
 * DendritView
 * Unlike conventional SNNs, the Dendrit network handles the weights
 *
 * @author Uwe Hennig
 */
public final class DendritView {
    // ----- lock/unlock -----

    public static void readLock(int index) {
        DendritModel model = DendritModelManager.instance().getModel();
        model.readLock(index);
    }

    public static void readUnlock(int index) {
        DendritModel model = DendritModelManager.instance().getModel();
        model.readUnlock(index);
    }

    public static void writeLock(int index) {
        DendritModel model = DendritModelManager.instance().getModel();
        model.writeLock(index);
    }

    public static void writeUnlock(int index) {
        DendritModel model = DendritModelManager.instance().getModel();
        model.writeUnlock(index);
    }

    // ----- getter/setter -----

    public static int getFieldId(int index) {
        DendritModel model = DendritModelManager.instance().getModel();
        return model.getFiedlId(index);
    }

    public static int getNeuronId(int index) {
        DendritModel model = DendritModelManager.instance().getModel();
        return model.getNeuronId(index);
    }

    public static int getSomaId(int index) {
        DendritModel model = DendritModelManager.instance().getModel();
        return model.getSomaId(index);
    }

    public static void setStructure(int index, int fieldId, int neuronId, int somaId) {
        DendritModel model = DendritModelManager.instance().getModel();
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setSomaId(index, somaId);
        } finally {
            model.writeUnlock(index);
        }
    }
}
