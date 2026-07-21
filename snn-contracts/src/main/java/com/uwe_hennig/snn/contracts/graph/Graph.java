/**
 * @(#)Graph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

import java.util.List;

/**
 * Graph
 *
 * @author Uwe Hennig
 */
public record Graph(List<Edge> edges) {
    public Graph addEdge(Edge edge) {
        edges.add(edge);
        return this;
    }

    public Graph addAllEdges(List<Edge> edgeList) {
        edges.addAll(edgeList);
        return this;
    }

    @Override
    public final String toString() {
        if (edges != null && !edges.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < edges.size(); i++) {
                builder.append(edges.get(i).shortString()).append("\n");
            }
            return builder.toString();
        }

        return "empty";
    }
}
