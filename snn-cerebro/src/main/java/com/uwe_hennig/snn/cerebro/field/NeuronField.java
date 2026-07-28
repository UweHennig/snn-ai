/**
 * @(#)NeuronField.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import static com.uwe_hennig.snn.contracts.core.NeuronFieldType.fieldType;

import java.util.AbstractList;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView.NeuronFieldData;
import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.core.ViewIdentity;

/**
 * NeuronField
 *
 * @author Uwe Hennig
 */
public class NeuronField extends ViewIdentity {
    private final int viewId;
    private final int neuronRef;
    private final int outRef;
    private final int inRef;

    public NeuronField(int viewId, int neuronRef, int outRef, int inRef) {
        assert neuronRef >= 0 && outRef >= 0 && inRef >= 0 && viewId >= 0;

        this.viewId = viewId;
        this.neuronRef = neuronRef;
        this.outRef = outRef;
        this.inRef = inRef;
    }

    private NeuronField(NeuronFieldView.NeuronFieldData data) {
        this.viewId = data.fieldId();
        this.neuronRef = data.neuronRef();
        this.outRef = data.outRef();
        this.inRef = data.inRef();
    }

    @Override
    public int getViewId() {
        return viewId;
    }

    public NeuronFieldType type() {
        return fieldType(NeuronFieldView.type(viewId));
    }

    // --- Neurons ---

    public List<Integer> getNeuronIds() {
        int[] neurons = NeuronFieldView.getNeuronIds(viewId);
        return asReadonlyList(neurons);
    }

    public void addNeurons(int... neuronIds) {
        NeuronFieldView.addNeuronIds(viewId, neuronIds);
    }

    // --- Out Neighbours ---

    public void addOutNeighbour(NeuronField field) {
        NeuronFieldView.addOutNeighbourIds(viewId, field.getViewId());
        NeuronFieldView.addInNeighbourIds(field.getViewId(), viewId);
    }

    public List<Integer> getOutNeighbourIds() {
        int[] out = NeuronFieldView.getOutNeighbourIds(viewId);
        return asReadonlyList(out);
    }

    // --- In Neighbours ---

    public void addInNeighbour(NeuronField field) {
        NeuronFieldView.addInNeighbourIds(viewId, field.getViewId());
    }

    public List<Integer> getInNeighbourIds() {
        int[] in = NeuronFieldView.getInNeighbourIds(viewId);
        return asReadonlyList(in);
    }

    private int[] getAllNeighbourIds(NeuronField current) {
        int[] in = NeuronFieldView.getInNeighbourIds(viewId);
        int[] out = NeuronFieldView.getOutNeighbourIds(viewId);

        int[] all = new int[in.length + out.length];

        System.arraycopy(in, 0, all, 0, in.length);
        System.arraycopy(out, 0, all, in.length, out.length);

        return all;
    }

    public void visit(Consumer<NeuronField> visitor) {
        Deque<Integer> queue = new ArrayDeque<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(this.getViewId());

        while (!queue.isEmpty()) {
            int currentIdx = queue.poll();

            if (visited.add(currentIdx)) {
                NeuronField current = createWrapper(currentIdx);
                visitor.accept(current);

                int[] all = getAllNeighbourIds(current);

                for (int next : all) {
                    queue.add(next);
                }
            }
        }
    }

    private static NeuronField createWrapper(int index) {
        NeuronFieldData data = NeuronFieldView.getData(index);
        return new NeuronField(data);
    }

    private static List<Integer> asReadonlyList(final int[] array) {
        return new AbstractList<Integer>() {
            @Override
            public Integer get(int index) {
                return array[index];
            }

            @Override
            public int size() {
                return array.length;
            }

            @Override
            public boolean add(Integer e) {
                throw new UnsupportedOperationException();
            }
        };
    }

}
