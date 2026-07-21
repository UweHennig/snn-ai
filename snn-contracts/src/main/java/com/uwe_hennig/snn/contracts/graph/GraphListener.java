/**
 * @(#)GraphListener.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

/**
 * GraphListener
 *
 * @author Uwe Hennig
 */
public interface GraphListener {
    void onNodeCreated(int id);
    void onEdgeCreated(int fromId, int toId);
}
