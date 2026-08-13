/**
 * @(#)DefaultAfferentGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;

/**
 * DefaultAfferentGraphGenerator
 *
 * @author Uwe Hennig
 */
public class DefaultAfferentGraphGenerator implements GraphGenerator {
    private int sizeNodes;

    public DefaultAfferentGraphGenerator(int sizeNodes) {
        this.sizeNodes = Math.max(sizeNodes, 2);
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment inputGraph) {
        RingGraphGenerator ringG = new RingGraphGenerator(NeuronFieldType.AFFERENT, sizeNodes);
        GraphFragments graph = ringG.generate(context, inputGraph);
        return graph;
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        RingGraphGenerator ringG = new RingGraphGenerator(NeuronFieldType.AFFERENT, sizeNodes);
        SingleGraphFragment graph = ringG.generate(context);
        return graph;
    }
}
