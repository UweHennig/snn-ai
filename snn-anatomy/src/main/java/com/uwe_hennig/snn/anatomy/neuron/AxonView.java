/// @(#)AxonView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// AxonView
///
/// @author Uwe Hennig
///
public final class AxonView {
    private AxonView() {
    }

    // ----- Getter/Setter -----

    public static int getFieldId(AxonModel model, int offset) {
        return model.getFiedlId(offset);
    }

    public static int getNeuronId(AxonModel model, int offset) {
        return model.getNeuronId(offset);
    }

    public static int getBlockId(AxonModel model, int offset) {
        return model.getBlockId(offset);
    }

    // ----- lock/unlock -----

    public static void readLock(AxonModel model, int offset) {
        model.readLock(offset);
    }

    public static void readUnlock(AxonModel model, int offset) {
        model.readUnlock(offset);
    }

    public static void writeLock(AxonModel model, int offset) {
        model.writeLock(offset);
    }

    public static void writeUnlock(AxonModel model, int offset) {
        model.writeUnlock(offset);
    }
}
