/**
 * @(#)PythagorasGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

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
    private int                   depth;
    private int                   sizeNodesA;
    private int                   sizeNodesB;

    public PythagorasGraphGenerator(NeuronFieldType type, int depth, int sizeNodesA, int sizeNodesB) {
        this.type = type;
        this.depth = depth;
        this.sizeNodesA = sizeNodesA;
        this.sizeNodesB = sizeNodesB;
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment graph) {
        Stack<SingleGraphFragment> stackA = new Stack<>();
        Stack<SingleGraphFragment> stackB = new Stack<>();
        stackB.push(graph);

        GraphFragments allFragments = GraphFragmentsImpl.create();

        for (int i = 0; i < depth; i++) {
            while(!stackA.isEmpty()) {
                SingleGraphFragment fragmentA = stackA.pop();
                for (Edge edge : fragmentA.edges()) {
                    if (context.isEdgeMarked(edge.edgeId())) {
                        continue;
                    }
                    context.markEdge(edge.edgeId());
                    int startNodeId = edge.nodeToId();
                    int endNodeId = edge.nodeFromId();

                    SingleGraphFragment sfragment = connect(context, startNodeId, endNodeId, sizeNodesB - 2, false);
                    stackB.push(sfragment);
                    allFragments.addFragement(sfragment);
                }
            }

            while (!stackB.isEmpty()) {
                SingleGraphFragment fragmentB = stackB.pop();
                for (Edge edge : fragmentB.edges()) {
                    if (context.isEdgeMarked(edge.edgeId())) {
                        continue;
                    }
                    context.markEdge(edge.edgeId());
                    int startNodeId = edge.nodeToId();
                    int endNodeId = edge.nodeFromId();

                    SingleGraphFragment sfragment = connect(context, startNodeId, endNodeId, sizeNodesA - 1, true);
                    stackA.push(sfragment);
                    allFragments.addFragement(sfragment);
                }

            }
        }

        return allFragments;
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        int initialNode = context.createNode(type);
        SingleGraphFragment fragment = connect(context, initialNode, initialNode, sizeNodesA - 1, false);
        GraphFragments resultFragments = generate(context, fragment);
        return resultFragments.meld();
    }

    private SingleGraphFragment connect(GenerationContext context, int nodeStart, int nodeEnd, int size, boolean mark) {
        SingleGraphFragment fragment = SingleGraphFragmentImpl.create();
        int currentNode = nodeStart;
        for (int i = 0; i < size; i++) {
            int nodeId = context.createNode(type);
            Edge edge = context.createEdge(currentNode, nodeId);
            if (mark && (i % 2 == 0)) {
                context.markEdge(edge.edgeId());
            }
            fragment.addEdge(edge);
            currentNode=nodeId;
        }
        Edge edge = context.createEdge(currentNode, nodeEnd);
        if (mark) {
            context.markEdge(edge.edgeId());
        }
        fragment.addEdge(edge);
        return fragment;
    }
}
