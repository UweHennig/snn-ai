/**
 * @(#)RingGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.graph.GraphFragmentsImpl;
import com.uwe_hennig.snn.graph.SingleGraphFragmentImpl;

/**
 * RingGraphGenerator For each edge, creates ‘sizeNodes’ nodes that form a ring inclusive the edge
 *
 * @author Uwe Hennig
 */
public class RingGraphGenerator implements GraphGenerator {
    private final NeuronFieldType     type;

    private int sizeNodes;

    public RingGraphGenerator(NeuronFieldType type, int sizeNodes) {
        assert sizeNodes > 2;
        assert type != null;

        this.sizeNodes = sizeNodes;
        this.type = type;
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        int startNode = context.createNode(type);
        int endNode = context.createNode(type);
        sizeNodes -= 2;

        Edge edge = context.createEdge(startNode, endNode);
        SingleGraphFragment singleFragment = SingleGraphFragmentImpl.create().addEdge(edge);

        GraphFragments fragments = generate(context, singleFragment);
        fragments.addFragement(singleFragment);

        return fragments.meld();
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment initialGraph) {
        GraphFragments graphFragments = GraphFragmentsImpl.create();

        for (Edge edge : initialGraph.edges()) {
            if (context.isUsedEdge(edge.edgeId())) {
                continue;
            }
            SingleGraphFragment singleFragment = SingleGraphFragmentImpl.create();

            int startNode = context.createNode(type);
            int currentNode = startNode;

            for (int i = 1; i < sizeNodes; i++) {
                int nextNode = context.createNode(type);
                Edge ringEdge = context.createEdge(currentNode, nextNode);
                singleFragment.addEdge(ringEdge);
                currentNode = nextNode;
            }

            context.setUsedEdge(edge.edgeId());

            Edge ringEdgeStart = context.createEdge(edge.nodeToId(), startNode);
            singleFragment.addEdge(ringEdgeStart);

            Edge ringEdgeEnd = context.createEdge(currentNode, edge.nodeFromId());
            singleFragment.addEdge(ringEdgeEnd);

            graphFragments.addFragement(singleFragment);
        }

        return graphFragments;
    }
}
