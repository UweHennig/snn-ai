/**
 * @(#)SomaView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.SomaModelMangager;

/**
 * SomaView
 *
 * @author Uwe Hennig
 */
public final class SomaView {
    // ----- lock/unlock -----

    public static void readLock(int index) {
        SomaModel model = SomaModelMangager.instance().getModel();
        model.readLock(index);
    }

    public static void readUnlock(int index) {
        SomaModel model = SomaModelMangager.instance().getModel();
        model.readUnlock(index);
    }

    public static void writeLock(int index) {
        SomaModel model = SomaModelMangager.instance().getModel();
        model.writeLock(index);
    }

    public static void writeUnlock(int index) {
        SomaModel model = SomaModelMangager.instance().getModel();
        model.writeUnlock(index);
    }

    // ----- Getter/Setter -----

    public static int getFieldId(int index) {
        SomaModel model = SomaModelMangager.instance().getModel();
        return model.getFieldId(index);
    }

    public static int getNeuronId(int index) {
        SomaModel model = SomaModelMangager.instance().getModel();
        return model.getNeuronId(index);
    }

    public static int getAxonId(int index) {
        SomaModel model = SomaModelMangager.instance().getModel();
        return model.getAxonId(index);
    }

    public static void setStructure(int index, int fieldId, int neuronId, int axonId) {
        SomaModel model = SomaModelMangager.instance().getModel();
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setAxonId(index, axonId);
        } finally {
            model.writeUnlock(index);
        }
    }
}
