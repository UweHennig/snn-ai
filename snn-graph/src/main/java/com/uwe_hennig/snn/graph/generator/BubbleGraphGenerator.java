/**
 * @(#)BubbleGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.graph.GraphFragmentsImpl;
import com.uwe_hennig.snn.graph.SingleGraphFragmentImpl;

/**
 * BubbleGraphGenerator
 *
 * @author Uwe Hennig
 */
public class BubbleGraphGenerator implements GraphGenerator {
    private final NeuronFieldType type;
    private final int             maxBubbleSize;
    private int                   depth;

    public BubbleGraphGenerator(NeuronFieldType type, int depth, int maxBubbleSize) {
        this.depth = depth;
        this.maxBubbleSize = maxBubbleSize;
        this.type = type;
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment graph) {
        Stack<SingleGraphFragment> stack = new Stack<>();
        stack.push(graph);

        GraphFragments depthFragemens = GraphFragmentsImpl.create();

        while (!stack.isEmpty() && depth-- > 0) {
            SingleGraphFragment singleFragment = stack.pop();
            List<Edge> filteredEdges = singleFragment.edges().stream().filter(e -> !context.isEdgeMarked(e.edgeId())).toList();
            if (filteredEdges.isEmpty()) {
                continue;
            }

            List<Edge> newEdges = new ArrayList<>();
            for (int e = 0; e < filteredEdges.size(); e++) {
                Edge edge = filteredEdges.get(e);
                context.markEdge(edge.edgeId());

                newEdges = loop(context, edge.nodeToId(), edge.nodeFromId(), randomBubbleLength());
            }
            SingleGraphFragment newFragment = SingleGraphFragmentImpl.create().addAllEdges(newEdges);

            stack.push(newFragment);
            depthFragemens.addFragement(newFragment);
        }

        return depthFragemens;
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        int startNode = context.createNode(type);
        int endNode = context.createNode(type);

        Edge edge = context.createEdge(startNode, endNode);
        SingleGraphFragment singleFragment = SingleGraphFragmentImpl.create().addEdge(edge);
        GraphFragments fragments = generate(context, singleFragment);
        return fragments.meld();
    }

    private List<Edge> loop(GenerationContext context, int startNode, int endNode, int sizeNodes) {
        List<Edge> edges = new ArrayList<>();
        int currentNode = startNode;

        for (int i = 0; i < sizeNodes; i++) {
            int next = context.createNode(type);
            edges.add(context.createEdge(currentNode, next));
            currentNode = next;
        }

        edges.add(context.createEdge(currentNode, endNode));

        return edges;
    }


    private int randomBubbleLength() {
        return ThreadLocalRandom.current().nextInt(2, maxBubbleSize);
    }

}
