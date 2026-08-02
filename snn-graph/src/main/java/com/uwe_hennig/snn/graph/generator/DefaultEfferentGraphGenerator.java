/**
 * @(#)DefaultEfferentGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.List;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.graph.util.GraphvizConsolePrinter;

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
    public List<Graph> generate(GenerationContext context, Graph initialGraph) {
        LeafRingConnectorGenerator generator = new LeafRingConnectorGenerator(NeuronFieldType.EFFERENT, nodes, mode);
        List<Graph> result = generator.generate(context, initialGraph);
        GraphvizConsolePrinter.printGraph(context, "DefaultEfferentGraphGenerator", Graph.create().addGraphs(result));
        return result;
    }

}
