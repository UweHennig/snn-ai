/**
 * @(#)NeuronView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

/**
 * NeuronView
 * @author Uwe Hennig
 */
public final class NeuronView {

    public static void setRefs(NeuronModel model, int offset, int fieldId, int dendritRef, int somaId, int axonId, int synapseRef) {
        model.setFieldId(offset, fieldId);
        model.setDendritRef(offset, dendritRef);
        model.setSomaId(offset, somaId);
        model.setAxonId(offset, axonId);
        model.setSynapseRef(offset, synapseRef);
    }
}
