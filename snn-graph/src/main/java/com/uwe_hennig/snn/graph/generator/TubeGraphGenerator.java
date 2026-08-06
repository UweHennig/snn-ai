/**
 * @(#)TubeGraphGenerator.java
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

/**
 * TubeGraphGenerator
 *
 * @author Uwe Hennig
 */
public class TubeGraphGenerator implements GraphGenerator {
    private NeuronFieldType type;
    private int ringSize;
    private int depth;

    public TubeGraphGenerator(NeuronFieldType type, int ringSize, int depth) {
        this.type = type;
        this.ringSize = ringSize;
        this.depth = depth;
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment graph) {
        RingGraphGenerator r1 = new RingGraphGenerator(type, ringSize);
        SingleGraphFragment fragment1 =  r1.generate(context);

        SingleGraphFragment resultSingle = generate(context, fragment1, graph, false);

        return GraphFragmentsImpl.create().addFragement(resultSingle);
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        RingGraphGenerator r1 = new RingGraphGenerator(type, ringSize);
        RingGraphGenerator r2 = new RingGraphGenerator(type, ringSize);

        SingleGraphFragment fragment1 =  r1.generate(context);
        SingleGraphFragment fragment2 =  r2.generate(context);
        return generate(context, fragment1, fragment2, true);
    }

    public SingleGraphFragment generate(GenerationContext context, SingleGraphFragment fragment1, SingleGraphFragment fragment2, boolean allEdges) {
        if (allEdges) {
            for (Edge edge : fragment1.edges()) {
                context.unmarkEdge(edge.edgeId());
            }

            for (Edge edge : fragment2.edges()) {
                context.unmarkEdge(edge.edgeId());
            }
        }

        GraphConcatenator con = new GraphConcatenator(type, fragment1, fragment2, depth);
        return con.generate(context);
    }
}
