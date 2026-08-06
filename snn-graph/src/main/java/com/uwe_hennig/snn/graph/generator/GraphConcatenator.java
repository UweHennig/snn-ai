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
    private int                   sizeNodes;

    public GraphConcatenator(NeuronFieldType type, SingleGraphFragment left, SingleGraphFragment right, int sizeNodes) {
        this.left = left;
        this.right = right;
        this.type = type;
        this.sizeNodes = sizeNodes;
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment right) {
        this.right = right;
        return GraphFragmentsImpl.create().addFragement(generate(context));
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        SingleGraphFragment resultFragement = SingleGraphFragmentImpl.create();

        List<Integer> leftNodes = getNodes(context, left, true);
        List<Integer> rightNodes = getNodes(context, right, false);

        int n = leftNodes.size();
        int m = rightNodes.size();

        if (n == 0 || m == 0) {
            return resultFragement;
        }

        for (int i = 0; i < Math.max(n, m); i++) {
            int startNode = leftNodes.get(i % n);
            int endNode = rightNodes.get(i % m);

            int currentNode = startNode;
            for (int j = 0; j < sizeNodes; j++) {
                int newNode = context.createNode(type);
                Edge edge = context.createEdge(currentNode, newNode);
                resultFragement.addEdge(edge);
                currentNode = newNode;
            }

            Edge edge = context.createEdge(currentNode, endNode);
            resultFragement.addEdge(edge);
        }

        return resultFragement;
    }

    private List<Integer> getNodes(GenerationContext context, SingleGraphFragment fragment, boolean nodeToId) {
        List<Integer> nodesIdList = new ArrayList<>();
        if (fragment == null) {
            return nodesIdList;
        }

        for (Edge edge : fragment.edges()) {
            if (!context.isEdgeMarked(edge.edgeId())) {
                int id = nodeToId ? edge.nodeToId() : edge.nodeFromId();
                nodesIdList.add(id);
                context.markEdge(edge.edgeId());
            }
        }
        return nodesIdList;
    }
}