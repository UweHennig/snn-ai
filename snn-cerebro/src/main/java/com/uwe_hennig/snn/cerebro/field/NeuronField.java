/**
 * @(#)NeuronField.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.uwe_hennig.snn.anatomy.core.MultiList;
import com.uwe_hennig.snn.anatomy.neuron.FieldModel;
import com.uwe_hennig.snn.anatomy.neuron.FieldView;

/**
 * NeuronField
 *
 * @author Uwe Hennig
 */
public class NeuronField {
    private final FieldView view;

    // TODO
    private int level;
    private int type;

    public NeuronField(FieldView view) {
        this.view = view;
    }

    public int getField() {
        return view.getViewId();
    }

    public List<NeuronField> getOutNeighbors() {
        // TODO
        return null;
    }

    public List<Integer> getNeurons() {
        // TODO
        return null;
    }

    public void addOutNeighbour(NeuronField node) {
        // TODO
    }

    public List<NeuronField> getOutNeighour() {
        // TODO
        return null;
    }

    public static void visit(NeuronField start, Consumer<NeuronField> visitor) {
        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        FieldModel model = start.view.getModel();
        MultiList multiList = start.view.getMultiList();

        queue.add(start.view.getViewId());
        while (!queue.isEmpty()) {
            int currentIdx = queue.poll();
            if (visited.add(currentIdx)) {
                NeuronField current = createWrapper(currentIdx, model, multiList);
                visitor.accept(current);
                for (int outIdx : current.view.getOutNeighbors()) {
                    queue.add(outIdx);
                }
            }
        }
    }

    private static NeuronField createWrapper(int index, FieldModel model, MultiList multiList) {
        FieldView tempView = new FieldView(index, model, multiList);
        return new NeuronField(tempView);
    }
}
