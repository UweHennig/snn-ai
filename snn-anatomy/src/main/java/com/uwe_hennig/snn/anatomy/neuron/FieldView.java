/**
 * @(#)FieldView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import java.util.Arrays;

import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * FieldView Field is currently a container for neuron fields and neurons.
 *
 * @author Uwe Hennig
 */
public final class FieldView {
    private static final long[] EMPTY_ARRAY = new long[0];

    private final FieldModel model;
    private final int        index;
    private final MultiList  multiList;

    public FieldView(int index, FieldModel model, MultiList multiList) {
        assert model != null : "Model must not bei null!";
        assert multiList != null : "MultiList must not bei null!";
        assert index < model.capacity && index >= 0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;
        this.multiList = multiList;
    }

    public FieldModel getModel() {
        return model;
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

    public long addNeuronId(long... neuronIds) {
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

        return neuronsRef;
    }

    public long[] getNeuronIds() {
        long neuronsRef = model.getNeuronRef(index);
        if (neuronsRef != -1) {
            return multiList.getLongs(neuronsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    // --- Out Neighbors ---

    public long[] getOutNeighborsRef() {
        long outNeighborsRef = model.getOutNeighborsRef(index);
        if (outNeighborsRef != -1) {
            return multiList.getLongs(outNeighborsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public long addOutNeighborsRef(long... outRefs) {
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
        updateIdentifiers(outNeighborsRef, outRefs);

        return outNeighborsRef;
    }

    // --- In Neighbors ---

    public long addInNeighborsRef(long... inRefs) {
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

        updateIdentifiers(inNeighborsRef, inRefs);

        return inNeighborsRef;
    }

    public long[] getInNeighborsRef() {
        long inNeighborsRef = model.getInNeighborsRef(index);

        if (inNeighborsRef != -1) {
            return multiList.getLongs(inNeighborsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    // --- Convenient ---

    private void updateIdentifiers(long ref, long... values) {
        long[] existing = multiList.getLongs(ref);
        long[] updated = add(existing, values);

        multiList.put(ref, updated);
    }

    private static long[] add(long[] existing, long... values) {
        // case 1
        if (values.length == 0) {
            long[] result = existing.clone();
            Arrays.sort(result);
            return result;
        }

        // case 2
        if (values.length == 1) {
            long singleValue = values[0];

            for (long val : existing) {
                if (val == singleValue) {
                    long[] result = existing.clone();
                    Arrays.sort(result);
                    return result;
                }
            }
            long[] result = new long[existing.length + 1];
            System.arraycopy(existing, 0, result, 0, existing.length);
            result[existing.length] = singleValue;
            Arrays.sort(result);
            return result;
        }

        // case 3
        long[] aSorted = existing.clone();
        Arrays.sort(aSorted);

        int newElementsCount = 0;
        for (int i = 0; i < values.length; i++) {
            if (Arrays.binarySearch(aSorted, values[i]) >= 0) {
                values[i] = -1;
            } else {
                newElementsCount++;
            }
        }

        long[] result = new long[existing.length + newElementsCount];
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
