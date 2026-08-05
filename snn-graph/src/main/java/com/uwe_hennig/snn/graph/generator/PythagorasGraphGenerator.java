/**
 * @(#)PythagorasGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.graph.GraphFragmentsImpl;
import com.uwe_hennig.snn.graph.SingleGraphFragmentImpl;

/**
 * PythagorasGraphGenerator
 *
 * @author Uwe Hennig
 */
public class PythagorasGraphGenerator implements GraphGenerator {
    private final NeuronFieldType type;
    private final int depth;
    private final int sizeNodesA;
    private final int sizeNodesB;

    public PythagorasGraphGenerator(NeuronFieldType type, int depth, int sizeNodesA, int sizeNodesB) {
        this.type = type;
        this.depth = depth;
        this.sizeNodesA = sizeNodesA;
        this.sizeNodesB = sizeNodesB;
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        int initialNode = context.createNode(type);
        SingleGraphFragment fragment = connect(context, initialNode, initialNode, sizeNodesA - 1, false);

        if (!fragment.edges().isEmpty()) {
            context.markEdge(fragment.edges().get(0).edgeId());
        }

        GraphFragments resultFragments = generate(context, fragment);
        return resultFragments.meld();
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment graph) {
        Stack<SingleGraphFragment> stackA = new Stack<>();
        Stack<SingleGraphFragment> stackB = new Stack<>();

        stackA.push(graph);
        GraphFragments allFragments = GraphFragmentsImpl.create();

        for (int i = 0; i < depth; i++) {
            List<SingleGraphFragment> nextA = new ArrayList<>();
            List<SingleGraphFragment> nextB = new ArrayList<>();

            // 1. Building from A to B
            while (!stackA.isEmpty()) {
                SingleGraphFragment fragmentA = stackA.pop();
                for (Edge edge : fragmentA.edges()) {
                    if (context.isEdgeMarked(edge.edgeId())) {
                        continue;
                    }
                    context.markEdge(edge.edgeId());

                    SingleGraphFragment sfragment = connect(context, edge.nodeToId(), edge.nodeFromId(), sizeNodesB - 2, true);
                    nextB.add(sfragment);
                    allFragments.addFragement(sfragment);
                }
            }

            // 2. Building from B to A
            while (!stackB.isEmpty()) {
                SingleGraphFragment fragmentB = stackB.pop();
                for (Edge edge : fragmentB.edges()) {
                    if (context.isEdgeMarked(edge.edgeId())) {
                        continue;
                    }
                    context.markEdge(edge.edgeId());

                    SingleGraphFragment sfragment = connect(context, edge.nodeToId(), edge.nodeFromId(), sizeNodesA - 2, false);
                    nextA.add(sfragment);
                    allFragments.addFragement(sfragment);
                }
            }

            stackA.addAll(nextA);
            stackB.addAll(nextB);
        }
        return allFragments;
    }

    /**
     * @param size: Number of new nodes to be created
     * @param isTypeB If true, even edge indices are highlighted (disabled)
     */
    private SingleGraphFragment connect(GenerationContext context, int nodeStart, int nodeEnd, int size, boolean isTypeB) {
        SingleGraphFragment fragment = SingleGraphFragmentImpl.create();
        int currentNode = nodeStart;
        List<Edge> newEdges = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            int nodeId = context.createNode(type);
            Edge edge = context.createEdge(currentNode, nodeId);
            fragment.addEdge(edge);
            newEdges.add(edge);
            currentNode = nodeId;
        }

        Edge lastEdge = context.createEdge(currentNode, nodeEnd);
        fragment.addEdge(lastEdge);
        newEdges.add(lastEdge);

        if (isTypeB) {
            for (int i = 0; i < newEdges.size(); i++) {
                if (i % 2 == 0) {
                    context.markEdge(newEdges.get(i).edgeId());
                }
            }
        }

        return fragment;
    }
}