/**
 * @(#)SnnMultiBitSet.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

/**
 * SnnMultiBitSet
 *
 * @author Uwe Hennig
 */
public class SnnMultiBitSet implements AutoCloseable {
    private static final int INITIAL_BITSET_SITZE = 8;
    private final int        numTypes;

    private SnnBitSet[]      bitSets;

    public SnnMultiBitSet(int numFields, int numTypes) {
        this.numTypes = numTypes;
        this.bitSets = new SnnBitSet[numFields * numTypes];
    }

    public void set(int field, int type, int index) {
        int idx = getInternalIndex(field, type);
        if (bitSets[idx] == null) {
            bitSets[idx] = new SnnBitSet(INITIAL_BITSET_SITZE);
        }
        bitSets[idx].set(index);
    }

    public void unset(int field, int type, int index) {
        int idx = getInternalIndex(field, type);
        if (bitSets[idx] == null) {
            bitSets[idx] = new SnnBitSet(INITIAL_BITSET_SITZE);
        }
        bitSets[idx].unset(index);
    }

    public boolean get(int field, int type, int index) {
        int idx = getInternalIndex(field, type);
        return bitSets[idx] != null && bitSets[idx].get(index);
    }

    private int getInternalIndex(int field, int type) {
        return field * numTypes + type;
    }

    @Override
    public void close() throws Exception {
        for (int i = 0; i < bitSets.length; i++) {
            if (bitSets[i] != null) {
                bitSets[i].close();
            }
        }
        bitSets = null;
    }
}
