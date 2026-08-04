/**
 * @(#)DefaultAfferentGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

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
    private int markUsedEdges;

    public DefaultAfferentGraphGenerator(int sizeNodes, int markUsedEdges) {
        this.sizeNodes = Math.max(sizeNodes, 2);
        this.markUsedEdges = Math.max(0, Math.min(sizeNodes, markUsedEdges));
    }

    @Override
    public GraphFragments generate(GenerationContext context, SingleGraphFragment graph) {
        // TODO
        return null;
    }

    @Override
    public SingleGraphFragment generate(GenerationContext context) {
        // TODO
        return null;
    }

}
