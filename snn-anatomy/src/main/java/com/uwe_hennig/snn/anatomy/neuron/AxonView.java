/**
 * @(#)AxonView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.AxonModelManager;

/**
 * AxonView
 *
 * @author Uwe Hennig
 */
public final class AxonView {
    // ----- lock/unlock -----

    public static void readLock(int index) {
        AxonModel model = AxonModelManager.instance().getModel();
        model.readLock(index);
    }

    public static void readUnlock(int index) {
        AxonModel model = AxonModelManager.instance().getModel();
        model.readUnlock(index);
    }

    public static void writeLock(int index) {
        AxonModel model = AxonModelManager.instance().getModel();
        model.writeLock(index);
    }

    public static void writeUnlock(int index) {
        AxonModel model = AxonModelManager.instance().getModel();
        model.writeUnlock(index);
    }

    // ----- Getter/Setter -----

    public static int getSynapseRef(int index) {
        AxonModel model = AxonModelManager.instance().getModel();
        return model.getSynapseRef(index);
    }

    public static int getNeuronId(int index) {
        AxonModel model = AxonModelManager.instance().getModel();
        return model.getNeuronId(index);
    }

    public static int getModulatorId(int index) {
        AxonModel model = AxonModelManager.instance().getModel();
        return model.getModulatorId(index);
    }

    public static void setStructure(int index, int fieldId, int neuronId, int synapseRef, int modulatorViewId) {
        AxonModel model = AxonModelManager.instance().getModel();
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setSynapseRef(index, synapseRef);
            model.setModulatorId(index, modulatorViewId);
        } finally {
            model.writeUnlock(index);
        }
    }
}
