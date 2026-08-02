/**
 * @(#)LeafRingConnectorGenerator.java
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
 * LeafRingConnectorGenerator
 * creates a ring from N new nodes and connect the ring with leafes
 *
 * @author Uwe Hennig
 */
public class LeafRingConnectorGenerator implements GraphGenerator {
    private final int               sizeNodes;
    private final EdgeDirectionMode mode;
    private final NeuronFieldType   type;
    private final Graph             resultingGraph = Graph.create();

    public LeafRingConnectorGenerator(NeuronFieldType type, int sizeNodes, EdgeDirectionMode edgeDirectionMode) {
        this.mode = edgeDirectionMode;
        this.sizeNodes = sizeNodes;
        this.type = type;
    }

    @Override
    public List<Graph> generate(GenerationContext context, Graph initialGraph) {
        RingGraphGenerator ringGen = new RingGraphGenerator(type, mode, sizeNodes);
        List<Graph> ringList = ringGen.generate(context, null);

        List<Edge> filteredNewEdges = ringList.stream().flatMap(g -> g.edges().stream()).filter(edge -> !context.isUsedEdge(edge.edgeId())).toList();
        int newModulus = filteredNewEdges.size();

        if (filteredNewEdges.isEmpty()) {
            System.err.println("No edges in ring generator found!");
            throw new RuntimeException("No edges in ring generator found!");
        }


        List<Edge> filterdInitialEdges = initialGraph.edges().stream().filter(edge -> !context.isUsedEdge(edge.edgeId())).toList();
        int initialModulus = filterdInitialEdges.size();

        if (filterdInitialEdges.isEmpty()) {
            System.err.println("No edges in initial graph found!");
            throw new RuntimeException("No edges in initial graph found!");
        }

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

        GraphvizConsolePrinter.printGraph(context, "LeafRingConnectorGenerator", resultingGraph);
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
