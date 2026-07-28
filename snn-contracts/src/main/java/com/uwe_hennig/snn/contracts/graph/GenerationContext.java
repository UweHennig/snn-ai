/**
 * @(#)GenerationContext.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

/**
 * GenerationContext
 *
 * @author Uwe Hennig
 */
public interface GenerationContext {
    // create Node and return node identifier
    int createNode(int type);

    long createEgeId(int srcNodeId);

    // connect two Nodes and return edge identifier
    long connect(int src, int trg);

    boolean isUsed(long edgeId);
    void setUsed(long edgeId);
}
