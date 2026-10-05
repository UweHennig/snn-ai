/// @(#)SomaView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// SomaView
///
/// @author Uwe Hennig
///
public final class SomaView {
    private SomaView() {
    }

    // ----- getter/setter -----

    public static int getFieldId(SomaModel model, int offset) {
        return model.getFieldId(offset);
    }

    public static int getNeuronId(SomaModel model, int offset) {
        return model.getNeuronId(offset);
    }

    public static int getAxonId(SomaModel model, int offset) {
        return model.getAxonId(offset);
    }

    // ----- lock/unlock -----

    public static void readLock(SomaModel model, int offset) {
        model.readLock(offset);
    }

    public static void readUnlock(SomaModel model, int offset) {
        model.readUnlock(offset);
    }

    public static void writeLock(SomaModel model, int offset) {
        model.writeLock(offset);
    }

    public static void writeUnlock(SomaModel model, int offset) {
        model.writeUnlock(offset);
    }
}
