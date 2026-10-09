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

    // ----- domain logic -----

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

    public static int getStpId(SomaModel model, int offset) {
        return model.getStpId(offset);
    }

    public static int getLtpId(SomaModel model, int offset) {
        return model.getLtpId(offset);
    }

    public static int getPotentialId(SomaModel model, int offset) {
        return model.getPotentialId(offset);
    }

    public static int getThresholdId(SomaModel model, int offset) {
        return model.getThresholdId(offset);
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
