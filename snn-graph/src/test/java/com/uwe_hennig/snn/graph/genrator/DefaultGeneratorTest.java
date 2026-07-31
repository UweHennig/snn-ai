/**
 * @(#)DefaultGeneratorTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.genrator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.graph.generator.DefaultAfferentGraphGenerator;
import com.uwe_hennig.snn.graph.generator.DefaultAssociativeGraphGenerator;
import com.uwe_hennig.snn.graph.util.GraphvizConsolePrinter;

/**
 * DefaultGeneratorTest
 *
 * @author Uwe Hennig
 */
public class DefaultGeneratorTest {
    public class GenerationContextTest implements GenerationContext {
        private int          nextNode = 0;
        public HashSet<Long> bitSet   = new HashSet<>();

        @Override
        public boolean isUsedEdge(long edgeId) {
            return bitSet.contains(edgeId);
        }

        @Override
        public void setUsedEdge(long edgeId) {
            bitSet.add(edgeId);
        }

        @Override
        public int createNode(NeuronFieldType type) {
            return nextNode++;
        }

        @Override
        public long createEdge(int src, int trg) {
            return packEdge(src, trg);
        }

        private long packEdge(int srcId, int trgId) {
            long edgeId = ((long) srcId << 32) | (trgId & 0xFFFFFFFFL);
            return edgeId;
        }
    }

    @Test
    @DisplayName("Afferent graph test")
    public void afferentTest() {
        final int graphs = 1;
        final int nodes = 3;
        final int markUsedEdges = 2;

        GenerationContextTest context = new GenerationContextTest();

        List<Graph> result = generateAfferentGraph(context, nodes, markUsedEdges);

        assertNotNull(result);
        assertEquals(graphs, result.size());
        assertEquals(nodes, result.get(0).edges().size());
        assertEquals(markUsedEdges, context.bitSet.size());
        GraphvizConsolePrinter.printToConsole(context, result.get(0));
    }

    @Test
    @DisplayName("Associative graph test")
    public void associativeTest() {
        GenerationContextTest context = new GenerationContextTest();

        Graph inputGraph = new Graph(new ArrayList<Edge>());
        int inNodeId = context.createNode(NeuronFieldType.ASSOCIATIVE);
        int outNodeId = context.createNode(NeuronFieldType.ASSOCIATIVE);
        long edgeId = context.createEdge(inNodeId, outNodeId);
        inputGraph.addEdge(new Edge(edgeId, inNodeId, outNodeId));

        DefaultAssociativeGraphGenerator generator = new DefaultAssociativeGraphGenerator(3, 2, 1);
        List<Graph> resultGraphs = generator.generate(context, inputGraph);
        resultGraphs.add(inputGraph);

        assertNotNull(resultGraphs);
        assertEquals(6, resultGraphs.size());

        Graph printGraph = Graph.create();
        printGraph.addGraphs(resultGraphs);
        GraphvizConsolePrinter.printToConsole(context, printGraph);
    }

    private List<Graph> generateAfferentGraph(GenerationContextTest context, int nodes, int markUsedEdges) {
        DefaultAfferentGraphGenerator generator = new DefaultAfferentGraphGenerator(nodes, markUsedEdges);
        return generator.generate(context, null);
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
