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

    public void addNeuron(NeuronFieldModel model, int offset, int neuronId) {
        model.addInField(offset, neuronId);
    }

    public void addInNeighbour(NeuronFieldModel model, int offset, int inNeighbour) {
        model.addInField(offset, inNeighbour);
    }

    public void addOutNeigbour(NeuronFieldModel model, int offset, int outNeigbour) {
        model.addOutField(offset, outNeigbour);
    }

    public void setFieldType(NeuronFieldModel model, int offset, int type) {
        model.setFieldType(offset, type);
    }

    int [] getAllFieldIds(NeuronFieldModel model) {
        return model.getFieldOffsets();
    }

    int [] getNeuronIds(NeuronFieldModel model, int offset) {
        return model.getNeuronIds(offset);
    }

    int [] getInNeighbours(NeuronFieldModel model, int offset) {
        return model.getInFields(offset);
    }

    int [] getOutNeighbours(NeuronFieldModel model, int offset) {
        return model.getOutFields(offset);
    }
}
