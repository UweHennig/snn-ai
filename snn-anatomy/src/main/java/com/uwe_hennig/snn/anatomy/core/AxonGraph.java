/**
 * @(#)AxonGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

/**
 * AxonGraph
 * @author Uwe Hennig
 */
public class AxonGraph {
    private static volatile AxonGraph INSTANCE;
    private final Blockchain blockchain;

    private AxonGraph(long maxFieldBlocks, int  minFieldSize) {
        blockchain = new Blockchain(maxFieldBlocks, minFieldSize);
    }

    public static AxonGraph of(long maxFieldBlocks, int  minFieldSize) {
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

    public long addSynapseIds(long[] synapseIds) {
        long ref = blockchain.allocate();
        blockchain.put(ref, synapseIds);
        return ref;
    }

    public long [] getSynapseIds(long synapseRef) {
        return blockchain.getLongs(synapseRef);
    }
}
