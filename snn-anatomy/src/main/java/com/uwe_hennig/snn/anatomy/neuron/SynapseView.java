/**
 * @(#)SynapseView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.SynapseModelManager;

/**
 * SynapseView
 *
 * @author Uwe Hennig
 */
public class SynapseView {
    // ----- lock/unlock -----

    public static void readLock(int index) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        model.readLock(index);
    }

    public static void readUnlock(int index) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        model.readUnlock(index);
    }

    public static void writeLock(int index) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        model.writeLock(index);
    }

    public static void writeUnlock(int index) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        model.writeUnlock(index);
    }
    // ----- getter/setter -----

    public static void setTargetRef(int index, int targetId) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        model.writeLock(index);
        model.setTargetId(index, targetId);
        model.writeUnlock(index);
    }


    public static int getFieldId(int index) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        return model.getFiedlId(index);
    }

    public static int getNeuronId(int index) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        return model.getNeuronId(index);
    }

    public static int getTargetId(int index) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        return model.getTargetId(index);
    }

    public static int getTargetType(int index) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        return model.getTargetType(index);
    }

    public static void setStructure(int index, int fieldId, int neuronId, int modulatorId) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        model.writeLock(index);
        try {
            model.setFieldId(index, fieldId);
            model.setNeuronId(index, neuronId);
            model.setModulatorId(index, modulatorId);
        } finally {
            model.writeUnlock(index);
        }
    }
}
