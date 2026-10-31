/// @(#)NeuronView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// NeuronView
///
/// @author Uwe Hennig
///
public final class NeuronView {

    public static void setFieldId(NeuronModel model, int offset, int fieldId) {
        model.setFieldId(offset, fieldId);
    }

    public static void setAttractionId(NeuronModel model, int offset, float attraction) {
        model.setAttraction(offset, attraction);
    }

    public static float getFieldId(NeuronModel model, int offset) {
        return model.getAttraction(offset);
    }
}
