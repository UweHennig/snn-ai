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

    public static void setRefs(int index, int fieldId, int dendritRef, int somaId, int axonId, int synapseRef) {
        NeuronModel model = NeuronModelManager.INSTANCE.getModel();
        model.setFieldId(index, fieldId);
        model.setRef(index, dendritRef, somaId, axonId, synapseRef);
    }
}
