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
    public static final int IN     = 1 << 0;
    public static final int OUT    = 1 << 1;
    public static final int IN_OUT = IN | OUT;

    private NeuronField() {
    }

    public static void traverse(int startFieldId, SnnFieldVisitor visitor, int mode) {
        assert visitor != null : "invalid parameter in NeuronField traverse";
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();

        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(startFieldId);

        while (!queue.isEmpty()) {
            int currentField = queue.poll();

            if (visited.add(currentField)) {
                if (visitor.visit(currentField)) {
                    return;
                }

                if ((mode & OUT) > 0) {
                    int[] out = NeuronFieldView.getOutNeighbours(model, currentField);
                    for (int id : out) {
                        queue.add(id);
                    }
                }

                if ((mode & IN) > 0) {
                    int[] in = NeuronFieldView.getInNeighbours(model, currentField);
                    for (int id : in) {
                        queue.add(id);
                    }
                }
            }
        }
    }

    public static void forEachField(Consumer<Integer> visitor) {
        NeuronFieldModel model = NeuronFieldModelManager.instance().getModel();
        int[] allFields = NeuronFieldView.getAllFieldIds(model);
        for (int i = 0; i < allFields.length; i++) {
            visitor.accept(allFields[i]);
        }
    }
}
