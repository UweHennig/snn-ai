/**
 * @(#)SingleGraphFragmentImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;

/**
 * SingleGraphFragmentImpl
 *
 * @author Uwe Hennig
 */
public class SingleGraphFragmentImpl implements SingleGraphFragment {
    private List<Edge> edges;

    protected SingleGraphFragmentImpl(List<Edge> edges) {
        this.edges = edges;
    }

    public static SingleGraphFragment create() {
        return new SingleGraphFragmentImpl(new ArrayList<>());
    }

    @Override
    public SingleGraphFragment addEdge(Edge edge) {
        if (edges.stream().filter(e -> e.equals(edge)).findFirst().isPresent()) {
            return this;
        }
        edges.add(edge);
        return this;
    }

    @Override
    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    @Override
    public String toString() {
        return edges.stream()
            .map(e -> e.toString())
            .collect(Collectors.joining(", ", "SingleGraphFragment[", "]"));
    }

    @Override
    public SingleGraphFragment addAllEdges(List<Edge> newEdges) {
        for (Edge edge : newEdges) {
            addEdge(edge);
        }
        return this;
    }

    @Override
    public int sizeEdges() {
        return edges.size();
    }

    @Override
    public int sizeNodes() {
        return (int) edges.stream()
            .flatMap(e -> Stream.of(e.nodeFromId(), e.nodeToId()))
            .distinct()
            .count();
    }

}
