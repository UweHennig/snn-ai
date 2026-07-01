/**
 * @(#)NeuronField.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldAllocator;
import com.uwe_hennig.snn.anatomy.core.MultiList;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;

/**
 * NeuronField
 *
 * @author Uwe Hennig
 */
public class NeuronField {
    private final NeuronFieldView view;

    private NeuronField(NeuronFieldView view) {
        this.view = view;
    }

    public static NeuronField newNeuronField(int type, int level) {
        NeuronFieldView view = NeuronFieldAllocator.instance().newFieldView(type, level);
        return new NeuronField(view);
    }

    public int getFieldId() {
        return view.getViewId();
    }

    public int level() {
        // TODO Enum
        return view.level();
    }

    public int type() {
        // TODO Enum
        return view.type();
    }

    public List<Integer> getNeuronIds() {
        int[] nIds = view.getNeuronIds();
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < nIds.length; i++) {
            result.add(nIds[i]);
        }
        return result;
    }

    public void addNeurons(int... neuronIds) {
        view.addNeuronId(neuronIds);
    }

    public void addOutNeighbour(NeuronField field) {
        view.addOutNeighbours(field.getFieldId());
        field.view.addInNeighbours(field.getFieldId());
    }

    public List<NeuronField> getOutNeighbours() {
        int[] out = view.getOutNeighbours();
        List<NeuronField> result = new ArrayList<>();

        for (int i = 0; i < out.length; i++) {
            result.add(createWrapper(out[i]));
        }

        return result;
    }

    public void addInNeighbour(NeuronField field) {
        view.addInNeighbours(field.getFieldId());
        field.view.addOutNeighbours(this.getFieldId());
    }

    public List<NeuronField> getInNeighbours() {
        int[] in = view.getInNeighbours();
        List<NeuronField> result = new ArrayList<>();

        for (int i = 0; i < in.length; i++) {
            result.add(createWrapper(in[i]));
        }

        return result;
    }

    public static void visit(NeuronField start, Consumer<NeuronField> visitor) {
        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(start.view.getViewId());
        while (!queue.isEmpty()) {
            int currentIdx = queue.poll();
            if (visited.add(currentIdx)) {
                NeuronField current = createWrapper(currentIdx);
                visitor.accept(current);
                for (int outIdx : current.view.getOutNeighbours()) {
                    queue.add(outIdx);
                }
            }
        }
    }

    private static NeuronField createWrapper(int index) {
        NeuronFieldView view = NeuronFieldAllocator.instance().viewAt(index);
        return new NeuronField(view);
    }
}
