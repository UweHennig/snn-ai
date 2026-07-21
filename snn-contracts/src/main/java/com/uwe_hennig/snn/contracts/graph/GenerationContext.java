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
    int nextNodeId();
    long connect(int src, int trg);

    boolean isUsed(int src, int trg);
    void setUsed(int src, int trg);
}
