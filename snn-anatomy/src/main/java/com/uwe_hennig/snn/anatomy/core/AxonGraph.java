/**
 * @(#)AxonGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

/**
 * AxonGraph
 *
 * @author Uwe Hennig
 */
public class AxonGraph {
    private static volatile AxonGraph INSTANCE;
    private final MultiList          blockchain;

    private AxonGraph(int maxFieldBlocks, int minFieldSize) {
        blockchain = new MultiList(maxFieldBlocks, minFieldSize);
    }

    public static AxonGraph of(int maxFieldBlocks, int minFieldSize) {
        synchronized (AxonGraph.class) {
            if (INSTANCE != null) {
                throw new IllegalStateException("Already initialized");
            }
            INSTANCE = new AxonGraph(maxFieldBlocks, minFieldSize);
            return INSTANCE;
        }
    }

    public static AxonGraph get() {
        return INSTANCE;
    }

    public int addSynapseIds(int[] synapseIds) {
        int ref = (int) blockchain.allocate();
        blockchain.put(ref, synapseIds);
        return ref;
    }

    public int[] getSynapseIds(int synapseRef) {
        return blockchain.getInts(synapseRef);
    }
}
