/**
 * @(#)DefaultAfferentGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph;

import java.util.ArrayList;
import java.util.List;

import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;

/**
 * DefaultAfferentGraphGenerator
 *
 * @author Uwe Hennig
 */
public class DefaultAfferentGraphGenerator implements GraphGenerator {

    @Override
    public List<Graph> generate(GenerationContext context, Graph initialGraph) {
        Graph graph = new Graph(new ArrayList<Edge>());

        int startNodeId = context.nextNodeId();
        int leftNodeId = context.nextNodeId();
        int rightNodeId = context.nextNodeId();

        long sl = context.connect(startNodeId, leftNodeId);
        graph.addEdge(new Edge(sl, startNodeId, leftNodeId));

        long ls = context.connect(leftNodeId, startNodeId);
        graph.addEdge(new Edge(ls, leftNodeId, startNodeId));

        long sr = context.connect(startNodeId, rightNodeId);
        graph.addEdge(new Edge(sr, startNodeId, rightNodeId));

        long rs = context.connect(rightNodeId, startNodeId);
        graph.addEdge(new Edge(rs, rightNodeId, startNodeId));

        long lr = context.connect(leftNodeId, rightNodeId);
        graph.addEdge(new Edge(lr, leftNodeId, rightNodeId));

        long rl = context.connect(rightNodeId, leftNodeId);
        graph.addEdge(new Edge(rl, rightNodeId, leftNodeId));

        return List.of(graph);
    }
}
