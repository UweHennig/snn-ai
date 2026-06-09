/**
 * @(#)FieldGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

/**
 * FieldGraph
 *
 * @author Uwe Hennig
 */
public class FieldGraph {
    private static volatile FieldGraph INSTANCE;
    private final Blockchain           blockchain;

    private FieldGraph(int maxFieldBlocks, int minFieldSize) {
        blockchain = new Blockchain(maxFieldBlocks, minFieldSize);
    }

    public static FieldGraph of(int maxFieldBlocks, int minFieldSize) {
        synchronized (FieldGraph.class) {
            if (INSTANCE != null) {
                throw new IllegalStateException("Already initialized");
            }
            INSTANCE = new FieldGraph(maxFieldBlocks, minFieldSize);
            return INSTANCE;
        }
    }

    public static FieldGraph get() {
        return INSTANCE;
    }

    public int addParentFieldIds(int[] parentIds) {
        int ref = (int) blockchain.allocate();
        blockchain.put(ref, parentIds);
        return ref;
    }

    public int addChildFieldIds(int[] childIds) {
        int ref = (int) blockchain.allocate();
        blockchain.put(ref, childIds);
        return ref;
    }

    public int addNeuronIds(int[] neuronIds) {
        int ref = (int) blockchain.allocate();
        blockchain.put(ref, neuronIds);
        return ref;
    }

    public int[] getParentFieldIds(int parentsRef) {
        return blockchain.getInts(parentsRef);
    }

    public int[] getChildFieldIds(int childrenRef) {
        return blockchain.getInts(childrenRef);
    }

    public int[] getNeuronIds(int neuronRef) {
        return blockchain.getInts(neuronRef);
    }

}
