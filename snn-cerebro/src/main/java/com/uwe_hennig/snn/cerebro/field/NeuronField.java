/**
 * @(#)NeuronField.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

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

    public static void traverse(int startFieldId, SnnFieldVisitor visitor, int mode) {
        assert visitor != null : "invalid parameter in NeuronField traverse";
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();

        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(startFieldId);

        while (!queue.isEmpty()) {
            int currentFieldId = queue.poll();

            if (visited.add(currentFieldId)) {
                if (visitor.visit(currentFieldId)) {
                    return;
                }

                int[] allFields = NeuronFieldView.getNeighbours(model, currentFieldId);
                for (int fieldId : allFields) {
                    queue.add(fieldId);
                }
            }
        }
    }

    public static void forEachField(int fieldId, Consumer<Integer> visitor) {
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();
        int[] allFields = NeuronFieldView.getNeighbours(model, fieldId);
        for (int i = 0; i < allFields.length; i++) {
            visitor.accept(allFields[i]);
        }
    }
}
