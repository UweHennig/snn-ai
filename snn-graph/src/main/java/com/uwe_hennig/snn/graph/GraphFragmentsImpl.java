/**
 * @(#)GraphFragmentsImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.Edge;

/**
 * GraphFragmentsImpl
 *
 * @author Uwe Hennig
 */
public class GraphFragmentsImpl implements GraphFragments {
    private final List<SingleGraphFragment> components;

    protected GraphFragmentsImpl(List<SingleGraphFragment> components) {
        this.components = components;
    }

    public static GraphFragments create() {
        return new GraphFragmentsImpl(new ArrayList<>());
    }

    @Override
    public List<SingleGraphFragment> fragments() {
        return Collections.unmodifiableList(components);
    }

    @Override
    public SingleGraphFragment meld() {
        Set<Edge> edgeSet = new HashSet<>();

        for (SingleGraphFragment cg : components) {
            edgeSet.addAll(cg.edges());
        }

        List<Edge> edgeList = new ArrayList<>(edgeSet);

        return new SingleGraphFragmentImpl(edgeList);
    }

    @Override
    public GraphFragments addFragement(SingleGraphFragment component) {
        components.add(component);
        return this;
    }

    @Override
    public GraphFragments addEdge(int id, Edge edge) {
        if (id >= components.size()) {
            return this;
        }

        components.get(id).addEdge(edge);

        return this;
    }

    @Override
    public String toString() {
        return components.stream().map(c -> c.toString())
            .collect(Collectors.joining(",\n", "GraphFragments{", "}"));
    }

    @Override
    public int sizeEdges() {
        return meld().sizeEdges();
    }

    @Override
    public int sizeNodes() {
        return meld().sizeNodes();
    }
}
