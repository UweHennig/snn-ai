/**
 * @(#)GenerationContext.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;

/**
 * GenerationContext
 *
 * @author Uwe Hennig
 */
public interface GenerationContext {
    // create Node and return node identifier
    int createNode(NeuronFieldType type);

    // connect two Nodes and return edge identifier
    long createEdge(int src, int trg);

    boolean isUsedEdge(long edgeId);
    void setUsedEdge(long edgeId);
}
