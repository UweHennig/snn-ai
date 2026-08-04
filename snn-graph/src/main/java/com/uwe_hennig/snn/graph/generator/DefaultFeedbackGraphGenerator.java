/**
 * @(#)DefaultFeedbackGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;

/**
 * DefaultFeedbackGraphGenerator
 *
 * @author Uwe Hennig
 */
public class DefaultFeedbackGraphGenerator implements GraphGenerator {
    private final int nodes;

    public DefaultFeedbackGraphGenerator(int nodes) {
        this.nodes = nodes;
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment graph) {
        // TODO Auto-generated method stub class GraphGenerator
        return null;
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        // TODO Auto-generated method stub class GraphGenerator
        return null;
    }
}
