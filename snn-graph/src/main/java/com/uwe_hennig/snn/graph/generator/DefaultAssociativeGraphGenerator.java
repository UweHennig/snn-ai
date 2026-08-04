/**
 * @(#)DefaultAssociativeGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.Stack;

import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;

/**
 * DefaultAssociativeGraphGenerator
 *
 * @author Uwe Hennig
 */
public class DefaultAssociativeGraphGenerator implements GraphGenerator {
    private final int depth;

    private Stack<SingleGraphFragment> stackA = new Stack<>();
    private Stack<SingleGraphFragment> stackB = new Stack<>();

    private RingGraphGenerator ringA;
    private RingGraphGenerator ringB;

    public DefaultAssociativeGraphGenerator(int depth, int nodesA, int nodesB) {
        this.depth = depth;
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
