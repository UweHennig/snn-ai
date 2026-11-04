/**
 * @(#)NeuronField.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldModelManager;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;
import com.uwe_hennig.snn.contracts.core.SnnFieldVisitor;

/**
 * NeuronField
 *
 * @author Uwe Hennig
 */
public class NeuronField {
    private NeuronField() {
    }

    public static void forEachField(SnnFieldVisitor visitor) {
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();
        int i = 0;
        int currentField = NeuronFieldView.getFieldAt(model, i++);

        while (currentField != -1) {
            if(!visitor.visit(currentField)) {
                return;
            }
            currentField = NeuronFieldView.getFieldAt(model, i++);
        }
    }
}
