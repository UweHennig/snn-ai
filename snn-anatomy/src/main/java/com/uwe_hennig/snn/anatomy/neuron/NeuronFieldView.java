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
        return model.getNeigbours(offset);
    }

    public static void addNeighbour(NeuronFieldModel model, int offset, int identifier) {
        int numNumNeighbours = model.getNumNeighbours(offset);
        model.setNumNeighbours(offset, numNumNeighbours + 1);
        model.setFieldId(offset, numNumNeighbours, identifier);
    }

    public static int getType(NeuronFieldModel model, int offset) {
        return model.getFieldType(offset);
    }

    public static void setType(NeuronFieldModel model, int offset, int type) {
        model.setFieldType(offset, type);
    }

}
