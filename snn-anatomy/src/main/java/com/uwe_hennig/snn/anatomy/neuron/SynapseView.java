/// @(#)SynapseView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// SynapseView
///
/// @author Uwe Hennig
///
public class SynapseView {
    private SynapseView() {
    }

    public static int claimSlot(SynapseModel model, int fieldId, int neuronId, int targetId) {
        return model.claimSlot(fieldId, neuronId, targetId);
    }

    // ----- getter/setter -----

    public static int getFiedlId(SynapseModel model, int offset) {
        return model.getFiedlId(offset);
    }

    public static int getNeuronId(SynapseModel model, int offset) {
        return model.getNeuronId(offset);
    }

    public static int getTargetId(SynapseModel model, int offset) {
        return model.getTargetId(offset);
    }

    public static int getModulatorId(SynapseModel model, int offset) {
        return model.getModulatorId(offset);
    }

    public static void setModulatorId(SynapseModel model, int offset, int value) {
        model.setModulatorId(offset, value);
    }

    // ----- lock/unlock -----

    public static void readLock(SynapseModel model, int offset) {
        model.readLock(offset);
    }

    public static void readUnlock(SynapseModel model, int offset) {
        model.readUnlock(offset);
    }

    public static void writeLock(SynapseModel model, int offset) {
        model.writeLock(offset);
    }

    public static void writeUnlock(SynapseModel model, int offset) {
        model.writeUnlock(offset);
    }
}
