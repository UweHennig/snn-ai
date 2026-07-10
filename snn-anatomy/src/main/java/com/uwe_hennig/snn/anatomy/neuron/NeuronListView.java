/**
 * @(#)NeuronListView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import java.util.Arrays;

import com.uwe_hennig.snn.anatomy.allocator.NeuronListManager;
import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * NeuronListView
 *
 * @author Uwe Hennig
 */
public class NeuronListView {
    public static final int [] EMPTY_ARRAY = new int [0];

    public static int [] getNeuronElementIds(int index, int neuronRef) {
        if (neuronRef != -1) {
            MultiList model = NeuronListManager.instance().getModel();
            return model.getInts(neuronRef);
        }

        return EMPTY_ARRAY;
    }

    public static void addNeuronElementId(int neuronRef, int ... elementIds) {
        if (neuronRef != -1) {
            MultiList model = NeuronListManager.instance().getModel();
            int [] existing = model.getInts(neuronRef);
            int [] update = add(existing, elementIds);

            model.put(neuronRef, update);
        }
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
