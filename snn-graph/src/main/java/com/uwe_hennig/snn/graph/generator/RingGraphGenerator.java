/**
 * @(#)RingGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.ArrayList;
import java.util.List;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;


/**
 * RingGraphGenerator
 *
 * @author Uwe Hennig
 */
public class RingGraphGenerator implements GraphGenerator {
    private final int sizeNodes;
    private final NeuronFieldType type;
    private final boolean markUsed;

    public RingGraphGenerator(NeuronFieldType type, int sizeNodes) {
        this.sizeNodes = sizeNodes;
        this.type = type;
        this.markUsed = sizeNodes > 1;
    }

    @Override
    public List<Graph> generate(GenerationContext context, Graph initialGraph) {
        if (initialGraph == null || initialGraph.edges().isEmpty()) {
            return generateInital(context);
        }

        Graph resultingGraph = new Graph(new ArrayList<Edge>());

        for (Edge edge : initialGraph.edges()) {
            if (context.isUsedEdge(edge.edgeId())) {
                continue;
            }

            Edge currentEdge = edge;
            context.setUsedEdge(currentEdge.edgeId());

            int startNodeId = currentEdge.nodeToId();
            int endNodeId = currentEdge.nodeFromId();

            int currentNodeId = startNodeId;

            for (int i = 0; i < sizeNodes; i++) {
                int newNodeId = context.createNode(type);
                long edgeId = context.createEdge(currentNodeId, newNodeId);
                if (i==0 && markUsed) {
                    context.setUsedEdge(edgeId);
                }

                resultingGraph.addEdge(new Edge(edgeId, currentNodeId, newNodeId));
                currentNodeId = newNodeId;
            }

            long edgeId = context.createEdge(currentNodeId, endNodeId);
            if (markUsed) {
                context.setUsedEdge(edgeId);
            }
            resultingGraph.addEdge(new Edge(edgeId, currentNodeId, endNodeId));
        }

        return List.of(resultingGraph);
    }

    private List<Graph> generateInital(GenerationContext context) {
        Graph resultingGraph = new Graph(new ArrayList<Edge>());

        int startNodeId = context.createNode(type);
        int currentNodeId = startNodeId;

        for (int i = 0; i < sizeNodes; i++) {
            int newNodeId = context.createNode(type);
            long edgeId = context.createEdge(currentNodeId, newNodeId);

            Edge edge = new Edge(edgeId, currentNodeId, newNodeId);
            resultingGraph.addEdge(edge);
            currentNodeId = newNodeId;
        }
        long edgeId = context.createEdge(currentNodeId, startNodeId);
        Edge edge = new Edge(edgeId, currentNodeId, startNodeId);
        resultingGraph.addEdge(edge);

        return List.of(resultingGraph);
    }
}
