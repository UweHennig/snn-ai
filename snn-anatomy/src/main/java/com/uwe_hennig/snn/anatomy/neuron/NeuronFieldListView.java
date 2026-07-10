/**
 * @(#)NeuronFieldListView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import java.util.Arrays;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldListManager;
import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * NeuronFieldListView
 *
 * @author Uwe Hennig
 */
public class NeuronFieldListView {
    public static final int [] EMPTY_ARRAY = new int [0];

    // --- Out Neighbours ---

    public static int[] getOutNeighbourIds(int outNeighboursRef) {
        MultiList listModel = NeuronFieldListManager.instance().getModel();

        if (outNeighboursRef != -1) {
            return listModel.getInts(outNeighboursRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public static void addOutNeighbourIds(int outNeighboursRef, int ... outFieldIds) {
        if (outNeighboursRef == -1) {
            return;
        }

        updateIdentifiers(outNeighboursRef, outFieldIds);
    }

    // --- Neurons ---

    public static int[] getNeuronIds(int neuronRef) {
        MultiList listModel = NeuronFieldListManager.instance().getModel();

        if (neuronRef != -1) {
            return listModel.getInts(neuronRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public static void addNeuronIds(int neuronsRef, int ... neuronIds) {
        if (neuronsRef == -1) {
            return;
        }
        updateIdentifiers(neuronsRef, neuronIds);
    }


    // --- In Neighbours ---

    public static int[] getInNeighbourIds(int inNeighboursRef) {
        MultiList listModel = NeuronFieldListManager.instance().getModel();

        if (inNeighboursRef != -1) {
            return listModel.getInts(inNeighboursRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public static void addInNeighbourIds(int inNeighboursRef, int ... inNodes) {
        if (inNeighboursRef == -1) {
            return;
        }
        updateIdentifiers(inNeighboursRef, inNodes);
    }

    // --- Convenient ---

    private static void updateIdentifiers(long ref, int ... values) {
        MultiList listModel = NeuronFieldListManager.instance().getModel();
        int[] existing = listModel.getInts(ref);
        int[] updated = add(existing, values);

        listModel.put(ref, updated);
    }

    /**
     * add
     * combines the arrays to a new one.
     * - duplicate int values are not supported!
     * - ordering is not guaranteed
     * @param existing values will be invalid after calling add
     * @param values will be invalid after calling add
     * @return new valid int array
     */
    private static int[] add(int[] existing, int ... values) {
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
