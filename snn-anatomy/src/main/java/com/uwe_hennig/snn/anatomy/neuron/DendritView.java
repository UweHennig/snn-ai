/// @(#)DendritView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// DendritView
/// Unlike conventional SNNs, the Dendrit network handles the weights
///
/// @author Uwe Hennig
///
public final class DendritView {
    private DendritView() {}

    // ----- getter/setter -----

    public static int getFieldId(DendritModel model, int offset) {
        return model.getFiedlId(offset);
    }

    public static int getNeuronId(DendritModel model, int offset) {
        return model.getNeuronId(offset);
    }

    public static int getSomaId(DendritModel model, int offset) {
        return model.getSomaId(offset);
    }

    // ----- lock/unlock -----

    public static void readLock(DendritModel model, int offset) {
        model.readLock(offset);
    }

    public static void readUnlock(DendritModel model, int offset) {
        model.readUnlock(offset);
    }

    public static void writeLock(DendritModel model, int offset) {
        model.writeLock(offset);
    }

    public static void writeUnlock(DendritModel model, int offset) {
        model.writeUnlock(offset);
    }

}
