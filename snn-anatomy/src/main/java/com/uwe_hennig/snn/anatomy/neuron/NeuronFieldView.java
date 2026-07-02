/**
 * @(#)NeuronFieldView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import java.util.Arrays;

import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * NeuronFieldView
 * is currently a container for fields and neurons.
 *
 * @author Uwe Hennig
 */
public final class NeuronFieldView {
    private static final int[] EMPTY_ARRAY = new int[0];
    private final int          index;

    private final NeuronFieldModel model;
    private final MultiList  multiList;

    public NeuronFieldView(int index, NeuronFieldModel model, MultiList multiList) {
        assert model != null : "Model must not bei null!";
        assert multiList != null : "MultiList must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.model = model;
        this.multiList = multiList;
        this.index = index;
    }

    public NeuronFieldModel getModel() {
        return model;
    }

    public MultiList getMultiList() {
        return multiList;
    }

    public int getViewId() {
        return index;
    }

    public int type() {
        return model.getType(index);
    }

    public void setType(int type) {
        try {
            model.writeLock(index);
            model.setType(index, type);
        } finally {
            model.writeUnlock(index);
        }
    }

    // --- Neurons ---

    public void addNeuronId(int ... neuronIds) {
        long neuronsRef = model.getNeuronRef(index);

        if (neuronsRef == -1) {
            neuronsRef = multiList.allocate();
            model.writeLock(index);
            try {
                model.setNeuronRef(index, neuronsRef);
            } finally {
                model.writeUnlock(index);
            }
        }

        updateIdentifiers(neuronsRef, neuronIds);
    }

    public int[] getNeuronIds() {
        long neuronsRef = model.getNeuronRef(index);
        if (neuronsRef != -1) {
            return multiList.getInts(neuronsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    // --- Out Neighbors ---

    public int[] getOutNeighbours() {
        long outNeighboursRef = model.getOutNeighborsRef(index);
        if (outNeighboursRef != -1) {
            return multiList.getInts(outNeighboursRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public void addOutNeighbours(int ... outNodes) {
        long outNeighboursRef = model.getOutNeighborsRef(index);

        if (outNeighboursRef == -1) {
            outNeighboursRef = multiList.allocate();
            model.writeLock(index);
            try {
                model.setOutNeighborsRef(index, outNeighboursRef);
            } finally {
                model.writeUnlock(index);
            }
        }
        updateIdentifiers(outNeighboursRef, outNodes);
    }

    // --- In Neighbors ---

    public void addInNeighbours(int ... inNodes) {
        long inNeighboursRef = model.getInNeighbourRef(index);

        if (inNeighboursRef == -1) {
            inNeighboursRef = multiList.allocate();
            model.writeLock(index);
            try {
                model.setInNeighborsRef(index, inNeighboursRef);
            } finally {
                model.writeUnlock(index);
            }
        }

        updateIdentifiers(inNeighboursRef, inNodes);
    }

    public int[] getInNeighbours() {
        long inNeighborsRef = model.getInNeighbourRef(index);

        if (inNeighborsRef != -1) {
            return multiList.getInts(inNeighborsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    // --- Convenient ---

    private void updateIdentifiers(long ref, int ... values) {
        int[] existing = multiList.getInts(ref);
        int[] updated = add(existing, values);

        multiList.put(ref, updated);
    }

    private static int[] add(int[] existing, int ... values) {
        // case 1
        if (values.length == 0) {
            int[] result = existing.clone();
            Arrays.sort(result);
            return result;
        }

        // case 2
        if (values.length == 1) {
            int singleValue = values[0];

            for (long val : existing) {
                if (val == singleValue) {
                    int[] result = existing.clone();
                    Arrays.sort(result);
                    return result;
                }
            }
            int[] result = new int[existing.length + 1];
            System.arraycopy(existing, 0, result, 0, existing.length);
            result[existing.length] = singleValue;
            Arrays.sort(result);
            return result;
        }

        // case 3
        int[] aSorted = existing.clone();
        Arrays.sort(aSorted);

        int newElementsCount = 0;
        for (int i = 0; i < values.length; i++) {
            if (Arrays.binarySearch(aSorted, values[i]) >= 0) {
                values[i] = -1;
            } else {
                newElementsCount++;
            }
        }

        int[] result = new int[existing.length + newElementsCount];
        System.arraycopy(aSorted, 0, result, 0, existing.length);

        int targetIndex = existing.length;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != -1) {
                result[targetIndex] = values[i];
                targetIndex++;
            }
        }

        Arrays.sort(result);
        return result;
    }

}
