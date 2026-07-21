/**
 * @(#)IdProvider.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

/**
 * IdProvider
 *
 * @author Uwe Hennig
 */
public interface IdProvider {
    int nextNodeId();
    int nextEdgeId();
}
