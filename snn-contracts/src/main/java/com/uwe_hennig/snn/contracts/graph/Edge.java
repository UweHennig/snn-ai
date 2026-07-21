/**
 * @(#)Edge.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

/**
 * Edge
 *
 * @author Uwe Hennig
 */
public record Edge(long edgeId, int nodeFromId, int nodeToId) {
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }

        if (!(other instanceof Edge otherEdge)) {
            return false;
        }

        return this.edgeId == otherEdge.edgeId;
    }

    public int hasCode() {
        return Long.hashCode(edgeId);
    }

    @Override
    public final String toString() {
        return String.format("%3d : %3d -> %3d", edgeId, nodeFromId, nodeToId());
    }

    public final String shortString() {
        return String.format("%3d -> %3d", nodeFromId, nodeToId());
    }
}
