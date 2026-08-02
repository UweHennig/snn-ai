/**
 * @(#)DefaultAfferentGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.List;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.graph.util.GraphvizConsolePrinter;

/**
 * DefaultAfferentGraphGenerator
 *
 * @author Uwe Hennig
 */
public class DefaultAfferentGraphGenerator implements GraphGenerator {
    private int sizeNodes;
    private int markUsedEdges;

    public DefaultAfferentGraphGenerator(int sizeNodes, int markUsedEdges) {
        this.sizeNodes = Math.max(sizeNodes, 2);
        this.markUsedEdges = Math.max(0, Math.min(sizeNodes, markUsedEdges));
    }

    @Override
    public List<Graph> generate(GenerationContext context, Graph initialGraph) {
        Graph graph = Graph.create();

        int startNodeId = context.createNode(NeuronFieldType.AFFERENT);
        int currentNodeId = startNodeId;
        for (int i = 0; i < sizeNodes - 1; i++) {
            int newNodeId = context.createNode(NeuronFieldType.AFFERENT);
            long edgeId = context.createEdge(currentNodeId, newNodeId);

            Edge edge = new Edge(edgeId, currentNodeId, newNodeId);
            graph.addEdge(edge);
            currentNodeId = newNodeId;
            if (markUsedEdges > 0) {
                context.setUsedEdge(edge.edgeId());
                markUsedEdges--;
            }
        }

        if (sizeNodes > 2) {
            long edgeId = context.createEdge(currentNodeId, startNodeId);
            Edge newEdge = new Edge(edgeId, currentNodeId, startNodeId);
            graph.addEdge(newEdge);
        }

        GraphvizConsolePrinter.printGraph(context, "DefaultAfferentGraphGenerator", graph);
        return List.of(graph);
    }
}
