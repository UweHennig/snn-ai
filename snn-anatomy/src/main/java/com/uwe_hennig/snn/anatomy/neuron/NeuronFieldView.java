/**
 * @(#)NeuronFieldView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldModelManager;

/**
 * NeuronFieldView
 *
 * @author Uwe Hennig
 */
public final class NeuronFieldView {
    public static record NeuronFieldData(int type, int fieldId, int neuronRef, int outRef, int inRef) {}

    public static int type(int index) {
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();
        return model.getType(index);
    }

    public static void setType(int index, int type) {
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();
        model.setType(index, type);
        model.setFieldId(index, index);
    }

    public static void resetRefs(int index) {
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();
        model.setNeuronRef(index, -1);
        model.setOutNeighboursRef(index, -1);
        model.setInNeighboursRef(index, -1);
    }

    public static void setRef(int index, int neuronRefs, int outRef, int inRef) {
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();
        model.setNeuronRef(index, neuronRefs);
        model.setOutNeighboursRef(index, outRef);
        model.setInNeighboursRef(index, inRef);
    }

    public static NeuronFieldData getData(int index) {
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();
        return new NeuronFieldData(
            model.getType(index),
            model.getFieldId(index),
            (int)model.getNeuronRef(index),
            (int)model.getOutNeighboursRef(index),
            (int)model.getInNeighbourRef(index)
        );
    }

}
