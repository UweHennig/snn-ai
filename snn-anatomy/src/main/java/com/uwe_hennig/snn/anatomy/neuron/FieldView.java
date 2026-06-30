/**
 * @(#)FieldView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import java.util.Arrays;

import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * FieldView
 * is currently a container for fields and neurons.
 *
 * @author Uwe Hennig
 */
public final class FieldView {
    private static final int[] EMPTY_ARRAY = new int[0];
    private final int          index;

    private final FieldModel model;
    private final MultiList  multiList;

    public FieldView(int index, FieldModel model, MultiList multiList) {
        assert model != null : "Model must not bei null!";
        assert multiList != null : "MultiList must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.model = model;
        this.multiList = multiList;
        this.index = index;
    }

    public FieldModel getModel() {
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

    public int level() {
        return model.getLevel(index);
    }

    public void setLevel(int level) {
        try {
            model.writeLock(index);
            model.setLevel(index, level);
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

    public int[] getOutNeighbors() {
        long outNeighborsRef = model.getOutNeighborsRef(index);
        if (outNeighborsRef != -1) {
            return multiList.getInts(outNeighborsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public void addOutNeighbors(int ... outNodes) {
        long outNeighborsRef = model.getOutNeighborsRef(index);

        if (outNeighborsRef == -1) {
            outNeighborsRef = multiList.allocate();
            model.writeLock(index);
            try {
                model.setOutNeighborsRef(index, outNeighborsRef);
            } finally {
                model.writeUnlock(index);
            }
        }
        updateIdentifiers(outNeighborsRef, outNodes);
    }

    // --- In Neighbors ---

    public void addInNeighbors(int ... inNodes) {
        long inNeighborsRef = model.getInNeighborsRef(index);

        if (inNeighborsRef == -1) {
            inNeighborsRef = multiList.allocate();
            model.writeLock(index);
            try {
                model.setInNeighborsRef(index, inNeighborsRef);
            } finally {
                model.writeUnlock(index);
            }
        }

        updateIdentifiers(inNeighborsRef, inNodes);
    }

    public int[] getInNeighbors() {
        long inNeighborsRef = model.getInNeighborsRef(index);

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
