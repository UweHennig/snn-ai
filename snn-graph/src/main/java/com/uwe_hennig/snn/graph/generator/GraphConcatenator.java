/**
 * @(#)GraphConcatenator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.ArrayList;
import java.util.List;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.graph.GraphFragmentsImpl;
import com.uwe_hennig.snn.graph.SingleGraphFragmentImpl;

/**
 * GraphConcatenator
 *
 * @author Uwe Hennig
 */
public class GraphConcatenator implements GraphGenerator {
    private final NeuronFieldType type;
    private SingleGraphFragment   left;
    private SingleGraphFragment   right;

    public GraphConcatenator(NeuronFieldType type, SingleGraphFragment left, SingleGraphFragment right) {
        this.left = left;
        this.right = right;
        this.type = type;
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment right) {
        this.right = right;

        SingleGraphFragment fragement = generate(context);
        GraphFragments gf = GraphFragmentsImpl.create().addFragement(fragement);
        return gf;
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        SingleGraphFragment resultFragement = SingleGraphFragmentImpl.create();

        List<Integer> leftNodes = getNodes(context, left, true);
        List<Integer> rightNodes = getNodes(context, right, false);

        int n = leftNodes.size();
        int m = rightNodes.size();

        RingGraphGenerator ringGraphGen = new RingGraphGenerator(type, n + m + 1);
        SingleGraphFragment ring = ringGraphGen.generate(context);
        List<Integer> ringNodes = getNodes(context, ring, true);

        int nodePos = 0;

        for (int i = 0; i < n; i++) {
            int startNode = leftNodes.get(i);
            int endNode = ringNodes.get(nodePos);

            Edge edge = context.createEdge(startNode, endNode);
            resultFragement.addEdge(edge);
            nodePos++;
        }

        for (int i = 0; i < m; i++) {
            int startNode = ringNodes.get(nodePos);
            int endNode = rightNodes.get(i);

            Edge edge = context.createEdge(startNode, endNode);
            resultFragement.addEdge(edge);
            nodePos++;
        }

        return resultFragement;
    }

    private List<Integer> getNodes(GenerationContext context, SingleGraphFragment fragment, boolean out) {
        List<Integer> nodesIdList = new ArrayList<>();
        for (Edge edge : fragment.edges()) {
            if (!context.isEdgeMarked(edge.edgeId())) {
                nodesIdList.add(out ? edge.nodeToId() : edge.nodeFromId());
                context.markEdge(edge.edgeId());
            }
        }
        return nodesIdList;
    }

}
