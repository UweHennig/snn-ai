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

    public static void addNeuron(NeuronFieldModel model, int offset, int neuronId) {
        model.addInField(offset, neuronId);
    }

    public static void addInNeighbour(NeuronFieldModel model, int offset, int inNeighbour) {
        model.addInField(offset, inNeighbour);
    }

    public static void addOutNeigbour(NeuronFieldModel model, int offset, int outNeigbour) {
        model.addOutField(offset, outNeigbour);
    }

    public static void setFieldType(NeuronFieldModel model, int offset, int type) {
        model.setFieldType(offset, type);
    }

    public static int [] getAllFieldIds(NeuronFieldModel model) {
        return model.getFieldOffsets();
    }

    public static int [] getNeuronIds(NeuronFieldModel model, int offset) {
        return model.getNeuronIds(offset);
    }

    public static int [] getInNeighbours(NeuronFieldModel model, int offset) {
        return model.getInFields(offset);
    }

    public static int [] getOutNeighbours(NeuronFieldModel model, int offset) {
        return model.getOutFields(offset);
    }

}
