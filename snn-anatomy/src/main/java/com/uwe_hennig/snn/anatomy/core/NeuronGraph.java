/**
 * @(#)NeuronGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

/**
 * NeuronGraph
 * @author Uwe Hennig
 */
public class NeuronGraph {
    private static volatile NeuronGraph INSTANCE;
    private final Blockchain blockchain;

    private NeuronGraph(long maxFieldBlocks, int  minFieldSize) {
        blockchain = new Blockchain(maxFieldBlocks, minFieldSize);
    }

    public static NeuronGraph of(long maxFieldBlocks, int  minFieldSize) {
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

    public long addNeuronElements(long[] neuronElements) {
        long ref = blockchain.allocate();
        blockchain.put(ref, neuronElements);
        return ref;
    }

    public long[] getNeuronElements(long neuronElementRef) {
        return blockchain.getLongs(neuronElementRef);
    }

}
