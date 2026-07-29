/**
 * @(#)NeuronFieldView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import java.util.Arrays;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldManager;
import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * NeuronFieldView
 *
 * @author Uwe Hennig
 */
public final class NeuronFieldView {
    public static final int[] EMPTY_ARRAY = new int[0];

    public static record NeuronFieldData(int fieldId, int type, int neuronRef, int outRef, int inRef) {
    }

    public static int type(int index) {
        NeuronFieldModel model = NeuronFieldManager.instance().getModel();
        return model.getType(index);
    }

    public static void setType(int index, int type) {
        NeuronFieldModel model = NeuronFieldManager.instance().getModel();
        model.setType(index, type);
        model.setFieldId(index, index);
    }

    public static void resetRefs(int index) {
        NeuronFieldModel model = NeuronFieldManager.instance().getModel();
        model.setNeuronRef(index, -1);
        model.setOutNeighboursRef(index, -1);
        model.setInNeighboursRef(index, -1);
    }

    public static void setRef(int index, int neuronRefs, int outRef, int inRef) {
        NeuronFieldModel model = NeuronFieldManager.instance().getModel();
        model.setNeuronRef(index, neuronRefs);
        model.setOutNeighboursRef(index, outRef);
        model.setInNeighboursRef(index, inRef);
    }

    public static NeuronFieldData getData(int index) {
        NeuronFieldModel model = NeuronFieldManager.instance().getModel();
        return new NeuronFieldData(model.getType(index), model.getFieldId(index), model.getNeuronRef(index), model.getOutNeighboursRef(index),
            model.getInNeighbourRef(index));
    }
    // --- Out Neighbours ---

    public static int[] getOutNeighbourIds(int index) {
        int outNeighboursRef = NeuronFieldManager.instance().getModel().getOutNeighboursRef(index);
        if (outNeighboursRef != -1) {
            MultiList multiList = NeuronFieldManager.instance().getListModel();
            return multiList.getInts(outNeighboursRef);
        }

        return EMPTY_ARRAY;
    }

    public static void addOutNeighbourIds(int index, int ... outFieldIds) {
        int outNeighboursRef = NeuronFieldManager.instance().getModel().getOutNeighboursRef(index);
        if (outNeighboursRef == -1) {
            return;
        }

        updateIdentifiers(outNeighboursRef, outFieldIds);
    }

    public static int [] getNeuronIds(int index) {
        int neuronRef = NeuronFieldManager.instance().getModel().getNeuronRef(index);
        if (neuronRef != -1) {
            MultiList multiList = NeuronFieldManager.instance().getListModel();
            return multiList.getInts(neuronRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public static void addNeuronIds(int index, int ... neuronIds) {
        int neuronRef = NeuronFieldManager.instance().getModel().getNeuronRef(index);
        updateIdentifiers(neuronRef, neuronIds);
    }

    // --- In Neighbours ---

    public static int[] getInNeighbourIds(int index) {
        int inNeighboursRef = NeuronFieldManager.instance().getModel().getInNeighbourRef(index);
        if (inNeighboursRef != -1) {
            MultiList multiList = NeuronFieldManager.instance().getListModel();
            return multiList.getInts(inNeighboursRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public static void addInNeighbourIds(int index, int... inNodes) {
        int inNeighboursRef = NeuronFieldManager.instance().getModel().getInNeighbourRef(index);
        if (inNeighboursRef == -1) {
            return;
        }
        updateIdentifiers(inNeighboursRef, inNodes);
    }

    public static int createNeuronField(int type) {
        NeuronFieldManager manger = NeuronFieldManager.instance();
        int index = manger.nextId();

        MultiList multiList = manger.getListModel();
        int neuronRef = (int) multiList.allocate();
        int outRef = (int) multiList.allocate();
        int inRef = (int) multiList.allocate();

        setRef(index, neuronRef, outRef, inRef);

        return index;
    }

    // --- Convenient ---

    private static void updateIdentifiers(long ref, int... values) {
        MultiList multiList = NeuronFieldManager.instance().getListModel();

        int[] existing = multiList.getInts(ref);
        int[] updated = add(existing, values);

        multiList.put(ref, updated);
    }

    /**
     * add combines the arrays to a new one. - duplicate int values are not supported! - ordering is not guaranteed
     *
     * @param existing
     *            values will be invalid after calling add
     * @param values
     *            will be invalid after calling add
     * @return new valid int array
     */
    private static int[] add(int[] existing, int... values) {
        // case 1
        if (values == null || values.length == 0) {
            if (existing == null) {
                return EMPTY_ARRAY;
            }
            return existing;
        }

        if (existing == null || existing.length == 0) {
            return values;
        }

        // case 2
        int[] small = existing.length >= values.length ? values : existing;
        int[] big = existing.length >= values.length ? existing : values;

        Arrays.sort(small);
        boolean[] foundInSmall = new boolean[small.length];

        int remainingSmall = small.length;

        for (int i = 0; i < big.length; i++) {
            int pos = Arrays.binarySearch(small, big[i]);
            // duplicates not allowed!
            if (pos >= 0) {
                foundInSmall[pos] = true;
                remainingSmall--;
                if (remainingSmall <= 0) {
                    return big;
                }
            }
        }

        // leftover

        int newElementsCount = 0;
        for (int value : small) {
            if (value != -1) {
                newElementsCount++;
            }
        }

        if (newElementsCount == 0) {
            // should never be executed
            return big;
        }

        int[] result = new int[big.length + newElementsCount];
        System.arraycopy(big, 0, result, 0, big.length);

        int j = 0;
        for (int i = big.length; i < result.length; i++) {
            while (j < small.length && small[j] == -1) {
                j++;
            }
            result[i] = small[j++];
        }

        return result;
    }
}
