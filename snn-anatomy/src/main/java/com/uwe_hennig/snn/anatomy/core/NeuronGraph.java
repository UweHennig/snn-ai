/**
 * @(#)NeuronGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

/**
 * NeuronGraph
 *
 * @author Uwe Hennig
 */
public class NeuronGraph {
    private static volatile NeuronGraph INSTANCE;
    private final Blockchain            blockchain;

    private NeuronGraph(int maxFieldBlocks, int minFieldSize) {
        blockchain = new Blockchain(maxFieldBlocks, minFieldSize);
    }

    public static NeuronGraph of(int maxFieldBlocks, int minFieldSize) {
        synchronized (NeuronGraph.class) {
            if (INSTANCE != null) {
                throw new IllegalStateException("Already initialized");
            }
            INSTANCE = new NeuronGraph(maxFieldBlocks, minFieldSize);
            return INSTANCE;
        }
    }

    public static NeuronGraph get() {
        return INSTANCE;
    }

    public int addNeuronElements(int[] neuronElements) {
        int ref = (int) blockchain.allocate();
        blockchain.put(ref, neuronElements);
        return ref;
    }

    public int[] getNeuronElements(int neuronElementRef) {
        return blockchain.getInts(neuronElementRef);
    }

}
