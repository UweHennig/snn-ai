/**
 * @(#)Graph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Graph
 *
 * @author Uwe Hennig
 */
public final class Graph {
    private final Set<Edge> edges;

    private Graph(Set<Edge> edges) {
        this.edges = edges;
    }

    public static Graph create() {
        return new Graph(new HashSet<>());
    }

    public Graph addEdge(Edge edge) {
        edges.add(edge);
        return this;
    }

    public Graph addAllEdges(Set<Edge> edgeList) {
        edges.addAll(edgeList);
        return this;
    }

    public Graph addGraphs(List<Graph> graphList) {
        for (Graph graph : graphList) {
            this.addAllEdges(graph.edges);
        }
        return this;
    }

    public Set<Edge> edges() {
        return Collections.unmodifiableSet(edges);
    }

    @Override
    public final String toString() {
        if (edges != null && !edges.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            Iterator<Edge> it = edges.iterator();
            while (it.hasNext()) {
                builder.append(it.next().toString()).append("\n");
            }
            return builder.toString();
        }

        return "empty";
    }

}
