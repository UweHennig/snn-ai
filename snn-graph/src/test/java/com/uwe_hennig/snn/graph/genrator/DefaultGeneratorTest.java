/**
 * @(#)DefaultGeneratorTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.genrator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import com.uwe_hennig.snn.graph.generator.EdgeDirectionMode;
import com.uwe_hennig.snn.graph.generator.LeafRingConnectorGenerator;
import com.uwe_hennig.snn.graph.generator.RingGraphGenerator;
import com.uwe_hennig.snn.graph.util.GraphvizConsolePrinter;

/**
 * DefaultGeneratorTest
 *
 * @author Uwe Hennig
 */
public class DefaultGeneratorTest {
    private Graph completeGraph = Graph.create();

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
            long edgeId = packEdge(src, trg);
            DefaultGeneratorTest.this.completeGraph.addEdge(new Edge(edgeId, src, trg));
            return edgeId;
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

        GraphvizConsolePrinter.printGraph(context, "Afferent Test", completeGraph);
    }

    @Test
    @DisplayName("Associative graph test")
    public void associativeTest() {
        GenerationContextTest context = new GenerationContextTest();

        Graph inputGraph = Graph.create();

        int inNodeId = context.createNode(NeuronFieldType.ASSOCIATIVE);
        int outNodeId = context.createNode(NeuronFieldType.ASSOCIATIVE);
        long edgeId = context.createEdge(inNodeId, outNodeId);
        inputGraph.addEdge(new Edge(edgeId, inNodeId, outNodeId));

        DefaultAssociativeGraphGenerator generator = new DefaultAssociativeGraphGenerator(3, 2, 1);
        List<Graph> resultGraphs = generator.generate(context, inputGraph);
        resultGraphs.add(inputGraph);

        assertNotNull(resultGraphs);
        assertEquals(6, resultGraphs.size());

        GraphvizConsolePrinter.printGraph(context, "Associative Test", completeGraph);
    }

    @Test
    @DisplayName("Ring graph test")
    public void ringGraphTest() {
        GenerationContextTest context = new GenerationContextTest();
        Graph inputGraph = Graph.create();

        int inNodeId = context.createNode(NeuronFieldType.ASSOCIATIVE);
        int outNodeId = context.createNode(NeuronFieldType.ASSOCIATIVE);

        long edgeId = context.createEdge(inNodeId, outNodeId);
        inputGraph.addEdge(new Edge(edgeId, inNodeId, outNodeId));

        RingGraphGenerator ringGGenerator = new RingGraphGenerator(NeuronFieldType.UNDEFINED, EdgeDirectionMode.FORWARD, 3);
        List<Graph> graphList = ringGGenerator.generate(context, inputGraph);
        assertNotNull(graphList);

        GraphvizConsolePrinter.printGraph(context, "Ring Test", completeGraph);
    }

    @Test
    @DisplayName("LeafRing graph test")
    public void testLeafRing() {
        GenerationContextTest context = new GenerationContextTest();
        RingGraphGenerator inputRing = new RingGraphGenerator(NeuronFieldType.UNDEFINED, EdgeDirectionMode.FORWARD, 3);

        List<Graph> graphList = inputRing.generate(context, null);
        Graph inputGraph = Graph.create().addGraphs(graphList);
        GraphvizConsolePrinter.printGraph(context, "LeafRing Test 1", completeGraph);

        LeafRingConnectorGenerator lrGenerator = new LeafRingConnectorGenerator(NeuronFieldType.UNDEFINED, 3, EdgeDirectionMode.FORWARD);
        List<Graph> resultList = lrGenerator.generate(context, inputGraph);
        assertNotNull(resultList);

        GraphvizConsolePrinter.printGraph(context, "LeafRing Test 2", completeGraph);
    }

    private List<Graph> generateAfferentGraph(GenerationContextTest context, int nodes, int markUsedEdges) {
        DefaultAfferentGraphGenerator generator = new DefaultAfferentGraphGenerator(nodes, markUsedEdges);
        return generator.generate(context, null);
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        completeGraph = Graph.create();

        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterEach
    public void afterEach() {
        completeGraph = null;
    }

    @BeforeAll
    public static void beforeAll() {
        System.setProperty("snn.logging", "true");
    }

    @AfterAll
    public static void afterAll() {
        System.setProperty("snn.logging", "false");
    }
}
