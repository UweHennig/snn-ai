/**
 * @(#)DefaultEfferentGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import com.uwe_hennig.snn.contracts.graph.EdgeDirectionMode;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;

/**
 * DefaultEfferentGraphGenerator
 *
 * @author Uwe Hennig
 */
public class DefaultEfferentGraphGenerator implements GraphGenerator {
    private final int               nodes;
    private final EdgeDirectionMode mode;

    public DefaultEfferentGraphGenerator(int nodes, EdgeDirectionMode mode) {
        this.nodes = nodes;
        this.mode = mode;
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
