/**
 * @(#)DefaultAfferentGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph;

import java.util.ArrayList;
import java.util.List;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
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
    private int type;
    private int sizeNodes;
    private int markUsedEdges;

    public DefaultAfferentGraphGenerator(int sizeNodes, int markUsedEdges) {
        this.type = NeuronFieldType.AFFERENT.code();
        this.sizeNodes = Math.max(sizeNodes, 2);
        this.markUsedEdges = Math.max(0, Math.min(sizeNodes - 1, markUsedEdges));
    }

    @Override
    public List<Graph> generate(GenerationContext context, List<Graph> initialGraph) {
        Graph graph = new Graph(new ArrayList<Edge>());

        int startNodeId = context.createNode(type);
        int currentNodeId = startNodeId;
        for (int i = 0; i < sizeNodes - 1; i++) {
            int newNodeId = context.createNode(type);
            long edgeId = context.connect(currentNodeId, newNodeId);
            Edge edge = new Edge(edgeId, currentNodeId, newNodeId);
            graph.addEdge(edge);
            currentNodeId = newNodeId;
        }

        int newNodeId = context.createNode(type);
        long edgeId = context.connect(currentNodeId, newNodeId);
        Edge newEdge = new Edge(edgeId, currentNodeId, startNodeId);
        graph.addEdge(newEdge);

        for (Edge edge : graph.edges()) {
            if (markUsedEdges > 0) {
                context.setUsed(edge.edgeId());
                markUsedEdges--;
            } else {
                break;
            }
        }

        return List.of(graph);
    }
}
