/**
 * @(#)Graph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

import java.util.ArrayList;
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

    public static Graph create() {
        return new Graph(new ArrayList<>());
    }

    public Graph addAllEdges(List<Edge> edgeList) {
        edges.addAll(edgeList);
        return this;
    }

    public Graph addGraphs(List<Graph> graphList) {
        for (Graph graph: graphList) {
            this.addAllEdges(graph.edges());
        }
        return this;
    }

    @Override
    public final String toString() {
        if (edges != null && !edges.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < edges.size(); i++) {
                builder.append(edges.get(i).toString()).append("\n");
            }
            return builder.toString();
        }

        return "empty";
    }
}
