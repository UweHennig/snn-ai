/**
 * @(#)LeafRingConnectorGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.EdgeDirectionMode;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.graph.GraphFragmentsImpl;
import com.uwe_hennig.snn.graph.SingleGraphFragmentImpl;

/**
 * LeafRingConnectorGenerator creates a ring from N new nodes and connect the ring with leafes
 *
 * @author Uwe Hennig
 */
public class LeafRingConnectorGenerator implements GraphGenerator {
    private final int                 sizeNodes;
    private final EdgeDirectionMode   mode;
    private final NeuronFieldType     type;

    public LeafRingConnectorGenerator(NeuronFieldType type, int sizeNodes, EdgeDirectionMode edgeDirectionMode) {
        this.mode = edgeDirectionMode;
        this.sizeNodes = sizeNodes;
        this.type = type;
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment graph) {
        RingGraphGenerator rgg = new RingGraphGenerator(type, sizeNodes);
        SingleGraphFragment ringGraph = rgg.generate(context);
        List<Edge> filteredInputEdges = graph.edges().stream().filter(e -> !context.isEdgeMarked(e.edgeId())).toList();
        SingleGraphFragment leafGraph = SingleGraphFragmentImpl.create().addAllEdges(filteredInputEdges);

        SingleGraphFragment singleResult;

        if (EdgeDirectionMode.FORWARD == mode) {
            singleResult = connect(context, ringGraph, leafGraph);
        } else {
            singleResult = connect(context, leafGraph, ringGraph);
        }

        return GraphFragmentsImpl.create().addFragement(singleResult);
    }

    private SingleGraphFragment connect(GenerationContext context, SingleGraphFragment fromGraph, SingleGraphFragment toGraph) {
        SingleGraphFragment singleResult = SingleGraphFragmentImpl.create();

        for (Edge edge: fromGraph.edges()) {
            int fromNode = edge.nodeToId();
            int toNode = randEdge(toGraph).nodeFromId();
            Edge newEdge = context.createEdge(fromNode, toNode);
            singleResult.addEdge(newEdge);
        }

        return singleResult;
    }

    private Edge randEdge(SingleGraphFragment fragment) {
        int size = fragment.edges().size();
        int randPos = ThreadLocalRandom.current().nextInt(size);
        return fragment.edges().get(randPos);
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        return SingleGraphFragmentImpl.create();
    }

}
