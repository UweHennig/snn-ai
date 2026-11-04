/// @(#)NeuronFieldView.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

/// NeuronFieldView
///
/// @author Uwe Hennig
///
public class NeuronFieldView {
    private NeuronFieldView() {
    }

    public static int [] getNeighbours(NeuronFieldModel model, int offset) {
        return model.getNeighbours(offset);
    }

    public static void addNeighbour(NeuronFieldModel model, int offset, int identifier) {
        model.addNeighbourId(offset, identifier);
    }

    public static int getType(NeuronFieldModel model, int offset) {
        return model.getFieldType(offset);
    }

    public static void setType(NeuronFieldModel model, int offset, int type) {
        model.setFieldType(offset, type);
    }

    public static void setAttraction(NeuronFieldModel model, int offset, float attraction) {
        model.setAttraction(offset, attraction);
    }

    public static float getAttraction(NeuronFieldModel model, int offset) {
        return model.getAttraction(offset);
    }
}
