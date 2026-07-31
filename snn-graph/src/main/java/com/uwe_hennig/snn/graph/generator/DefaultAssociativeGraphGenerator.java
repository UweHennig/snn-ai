/**
 * @(#)DefaultAssociativeGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;



/**
 * DefaultAssociativeGraphGenerator
 *
 * @author Uwe Hennig
 */
public class DefaultAssociativeGraphGenerator implements GraphGenerator {
    private final int depth;

    private Stack<Graph> stackA = new Stack<>();
    private Stack<Graph> stackB = new Stack<>();

    private RingGraphGenerator ringA;
    private RingGraphGenerator ringB;

    public DefaultAssociativeGraphGenerator(int depth, int nodesA, int nodesB) {
        this.depth = depth;
        ringA = new RingGraphGenerator(NeuronFieldType.ASSOCIATIVE, nodesA);
        ringB = new RingGraphGenerator(NeuronFieldType.ASSOCIATIVE, nodesB);
    }

    @Override
    public List<Graph> generate(GenerationContext context, Graph initialGraph) {
        stackB.push(initialGraph);

        List<Graph> resultingGraphs = new ArrayList<>();

        for (int i = 0; i < depth; i++) {
            // Phase A -> B
            while (!stackA.isEmpty()) {
                Graph currentGraph = stackA.pop();
                List<Graph> nextGraphLayer = ringB.generate(context, currentGraph);

                if (nextGraphLayer != null) {
                    resultingGraphs.addAll(nextGraphLayer);
                    for (Graph graph : nextGraphLayer) {
                        stackB.push(graph);
                    }
                }
            }

            // Phase B -> A
            while (!stackB.isEmpty()) {
                Graph currentGraph = stackB.pop();
                List<Graph> nextGraphLayer = ringA.generate(context, currentGraph);

                if (nextGraphLayer != null) {
                    resultingGraphs.addAll(nextGraphLayer);
                    for (Graph graph : nextGraphLayer) {
                        stackA.push(graph);
                    }
                }
            }
        }


        return resultingGraphs;
    }

}
