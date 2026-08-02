/**
 * @(#)DefaultFeedbackGraphGenerator.java
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
    public List<Graph> generate(GenerationContext context, Graph initialGraph) {
        RingGraphGenerator ring = new RingGraphGenerator(NeuronFieldType.FEEDBACK, EdgeDirectionMode.FORWARD, nodes);
        List<Graph> graphs = ring.generate(context, null);
        Graph ringGraph = Graph.create().addGraphs(graphs);

        LeafRingConnectorGenerator leafGen = new LeafRingConnectorGenerator(NeuronFieldType.FEEDBACK, 1, EdgeDirectionMode.FORWARD);

        List<Graph> result = leafGen.generate(context, ringGraph);
        GraphvizConsolePrinter.printGraph(context, "DefaultFeedbackGraphGenerator", Graph.create().addGraphs(result));
        return result;
    }

}
