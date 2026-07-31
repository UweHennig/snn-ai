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
 * RingGraphGenerator For each edge, creates ‘sizeNodes’ nodes that form a ring inclusive the edge
 *
 * @author Uwe Hennig
 */
public class RingGraphGenerator implements GraphGenerator {
    private final int                sizeNodes;
    private final NeuronFieldType    type;
    private final boolean            markUsed;
    private final EdgeDirectionMode mode;
    private final Graph              resultingGraph;

    public RingGraphGenerator(NeuronFieldType type, EdgeDirectionMode mode, int sizeNodes) {
        this.sizeNodes = sizeNodes;
        this.type = type;
        this.markUsed = sizeNodes > 1;
        this.mode = mode;
        this.resultingGraph = Graph.create();
    }

    @Override
    public List<Graph> generate(GenerationContext context, Graph initialGraph) {
        if (initialGraph == null || initialGraph.edges().isEmpty()) {
            return generateInital(context);
        }

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
                Edge newEdge = createEdge(context, currentNodeId, newNodeId);

                if (i == 0 && markUsed) {
                    context.setUsedEdge(newEdge.edgeId());
                }

                currentNodeId = newNodeId;
            }

            Edge newEdge = createEdge(context, currentNodeId, endNodeId);
            if (markUsed) {
                context.setUsedEdge(newEdge.edgeId());
            }
        }

        return List.of(resultingGraph);
    }

    private List<Graph> generateInital(GenerationContext context) {
        Graph resultingGraph = new Graph(new ArrayList<Edge>());

        int startNodeId = context.createNode(type);
        int currentNodeId = startNodeId;

        for (int i = 0; i < sizeNodes - 1; i++) {
            int newNodeId = context.createNode(type);
            Edge edge = createEdge(context, currentNodeId, newNodeId);
            resultingGraph.addEdge(edge);

            currentNodeId = newNodeId;
        }

        Edge edge = createEdge(context, currentNodeId, startNodeId);
        resultingGraph.addEdge(edge);

        return List.of(resultingGraph);
    }

    private Edge createEdge(GenerationContext context, int fromNode, int toNode) {
        Edge resultEdge = null;
        if (mode == EdgeDirectionMode.FORWARD || mode == EdgeDirectionMode.BOTH) {
            long edgeId = context.createEdge(fromNode, toNode);
            Edge edge = new Edge(edgeId, fromNode, toNode);
            resultingGraph.addEdge(edge);
            resultEdge = edge;
        }

        if (mode == EdgeDirectionMode.BACKWARDS || mode == EdgeDirectionMode.BOTH) {
            long edgeId = context.createEdge(toNode, fromNode);
            Edge edge = new Edge(edgeId, toNode, fromNode);
            resultingGraph.addEdge(edge);
            if (resultEdge == null) {
                resultEdge = edge;
            } else {
                context.setUsedEdge(edgeId);
            }
        }

        return resultEdge;
    }

}
