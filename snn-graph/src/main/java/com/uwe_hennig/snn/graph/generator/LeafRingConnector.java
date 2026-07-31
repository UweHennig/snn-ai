/**
 * @(#)LeafRingConnector.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.List;
import java.util.stream.Collectors;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;

/**
 * LeafRingConnector creates a ring from N new nodes and connect the ring with leaves
 *
 * @author Uwe Hennig
 */
public class LeafRingConnector implements GraphGenerator {
    private final int               sizeNodes;
    private final EdgeDirectionMode mode;
    private final NeuronFieldType   type;
    private final Graph             resultingGraph = Graph.create();

    public LeafRingConnector(NeuronFieldType type, int sizeNodes, EdgeDirectionMode edgeDirectionMode) {
        this.mode = edgeDirectionMode;
        this.sizeNodes = sizeNodes;
        this.type = type;
    }

    @Override
    public List<Graph> generate(GenerationContext context, Graph initialGraph) {
        RingGraphGenerator ringGen = new RingGraphGenerator(type, mode, sizeNodes);
        List<Graph> graphList = ringGen.generate(context, null);

        List<Edge> filteredNewEdges = graphList.stream().flatMap(g -> g.edges().stream()).filter(edge -> !context.isUsedEdge(edge.edgeId())).toList();
        int newModulus = filteredNewEdges.size();

        List<Edge> filterdInitialEdges = initialGraph.edges().stream().filter(edge -> !context.isUsedEdge(edge.edgeId())).toList();
        int initialModulus = filterdInitialEdges.size();

        boolean forward = mode == EdgeDirectionMode.FORWARD || mode == EdgeDirectionMode.BOTH;

        int interations = forward ? newModulus : initialModulus;

        for (int i = 0; i < interations; i++) {
            Edge newEdge = filteredNewEdges.get(i % newModulus);
            Edge iniEdge = filterdInitialEdges.get(i % initialModulus);

            int fromNode = forward ? newEdge.nodeToId() : iniEdge.nodeToId();
            int toNode = forward ? iniEdge.nodeToId() : newEdge.nodeFromId();

            context.setUsedEdge(newEdge.edgeId());
            context.setUsedEdge(iniEdge.edgeId());

            createEdge(context, fromNode, toNode);
        }

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
