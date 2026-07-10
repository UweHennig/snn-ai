/**
 * @(#)NeuronView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.NeuronModelManager;

/**
 * NeuronView
 * @author Uwe Hennig
 */
public final class NeuronView {
    public static void setFieldId(int index, int fieldId) {
        NeuronModel model = NeuronModelManager.INSTANCE.getModel();
        model.setFieldId(index, fieldId);
    }

    public static void setNeuronElementRef(int index, int neuronElementRef) {
        NeuronModel model = NeuronModelManager.INSTANCE.getModel();
        model.setNeuronElementRef(index, neuronElementRef);
    }
}
