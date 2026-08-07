/**
 * @(#)TubeGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

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
 * TubeGraphGenerator
 *
 * @author Uwe Hennig
 */
public class TubeGraphGenerator implements GraphGenerator {
    private NeuronFieldType    type;
    private int                ringSize;
    private int                depth;
    private RingGraphGenerator ringGen;

    public TubeGraphGenerator(NeuronFieldType type, int ringSize, int depth) {
        this.type = type;
        this.ringSize = ringSize;
        this.depth = depth;
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment graph) {
        // TODO
        return GraphFragmentsImpl.create().addFragement(generate(context));
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        ringGen = new RingGraphGenerator(type, ringSize);
        SingleGraphFragment srcFragment = null;
        SingleGraphFragment trgFragment = null;

        GraphFragments fragments = GraphFragmentsImpl.create();
        srcFragment =  ringGen.generate(context);

        for (int i = 0; i < depth - 1; i++) {
            trgFragment = ringGen.generate(context);

            fragments.addFragement(srcFragment);
            fragments.addFragement(trgFragment);

            SingleGraphFragment conFragment = createLayer(context, srcFragment, trgFragment);
            fragments.addFragement(conFragment);

            srcFragment = trgFragment;
        }

        return fragments.meld();
    }

    private SingleGraphFragment createLayer(GenerationContext context, SingleGraphFragment srcFragment, SingleGraphFragment trgFragment) {
        List<Edge> srcEdges = srcFragment.edges();
        List<Edge> trgEdges = trgFragment.edges();

        SingleGraphFragment connection = SingleGraphFragmentImpl.create();

        for (int j = 0; j < srcEdges.size(); j++) {
            Edge srcEdge = srcEdges.get(j);
            Edge trgEdge = trgEdges.get(j % trgEdges.size());
            context.markEdge(srcEdge.edgeId());

            int srcNode = srcEdge.nodeToId();
            int trgNode = trgEdge.nodeFromId();

            Edge newEdge = context.createEdge(srcNode, trgNode);
            context.markEdge(newEdge.edgeId());
            connection.addEdge(newEdge);
        }

        return connection;
    }
}
