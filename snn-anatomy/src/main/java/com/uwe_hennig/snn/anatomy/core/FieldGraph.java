/**
 * @(#)FieldGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

/**
 * FieldGraph
 * @author Uwe Hennig
 */
public class FieldGraph {
    private static volatile FieldGraph INSTANCE;
    private final Blockchain blockchain;

    private FieldGraph(long maxFieldBlocks, int  minFieldSize) {
        blockchain = new Blockchain(maxFieldBlocks, minFieldSize);
    }

    public static FieldGraph of(long maxFieldBlocks, int  minFieldSize) {
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

    public long addParentFieldIds(long[] parentIds) {
        long ref = blockchain.allocate();
        blockchain.put(ref, parentIds);
        return ref;
    }

    public long addChildFieldIds(long[] childIds) {
        long ref = blockchain.allocate();
        blockchain.put(ref, childIds);
        return ref;
    }

    public long addNeuronIds(long[] neuronIds) {
        long ref = blockchain.allocate();
        blockchain.put(ref, neuronIds);
        return ref;
    }

    public long[] getParentFieldIds(long parentsRef) {
        return blockchain.getLongs(parentsRef);
    }

    public long[] getChildFieldIds(long childrenRef) {
        return blockchain.getLongs(childrenRef);
    }

    public long[] getNeuronIds(long neuronRef) {
        return blockchain.getLongs(neuronRef);
    }

}
