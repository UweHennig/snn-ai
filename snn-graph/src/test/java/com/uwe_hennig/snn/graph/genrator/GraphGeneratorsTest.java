/**
 * @(#)GraphGeneratorsTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.genrator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.EdgeDirectionMode;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphFragments;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.graph.SingleGraphFragmentImpl;
import com.uwe_hennig.snn.graph.generator.BubbleGraphGenerator;
import com.uwe_hennig.snn.graph.generator.LeafRingConnectorGenerator;
import com.uwe_hennig.snn.graph.generator.RingGraphGenerator;
import com.uwe_hennig.snn.graph.util.GraphvizConsolePrinter;

/**
 * GraphGeneratorsTest
 *
 * @author Uwe Hennig
 */
public class GraphGeneratorsTest {
    private SingleGraphFragment   completeGraph;
    private GenerationContextTest context;

    // Initially without OffHeap
    public class GenerationContextTest implements GenerationContext {
        private int          nextNode = 0;
        private long         nextEdge = 0;
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
        public Edge createEdge(int src, int trg) {
            long edgeId = packEdge(src, trg);
            Edge edge = new Edge(edgeId, src, trg);
            completeGraph.addEdge(edge);
            return edge;
        }

        private long packEdge(int srcId, int trgId) {
            return nextEdge++;
        }
    }

    @Test
    @DisplayName("Ring single graph test")
    public void testSingleRingGraph() {
        RingGraphGenerator rgg = new RingGraphGenerator(NeuronFieldType.UNDEFINED, 3);
        SingleGraphFragment fragment = rgg.generate(context);
        assertNotNull(fragment);
        assertEquals(3, fragment.edges().size());

        GraphvizConsolePrinter.printGraph(context, "Ring single graph test", completeGraph);
    }

    @Test
    @DisplayName("Ring graph test")
    public void testRingGraph() {
        RingGraphGenerator rgg3 = new RingGraphGenerator(NeuronFieldType.UNDEFINED, 3);
        SingleGraphFragment fragment = rgg3.generate(context);
        assertNotNull(fragment);
        assertEquals(3, fragment.edges().size());

        RingGraphGenerator rgg4 = new RingGraphGenerator(NeuronFieldType.UNDEFINED, 4);

        GraphFragments fragements = rgg4.generate(context, fragment);
        assertNotNull(fragements);
        // 4 -> 5 Edges * 2 input Edges
        assertEquals(10, fragements.meld().edges().size());

        GraphvizConsolePrinter.printGraph(context, "Ring graph test", completeGraph);
    }

    @Test
    @DisplayName("Bubble graph test")
    public void testBubbleGraph() {
        BubbleGraphGenerator bgg = new BubbleGraphGenerator(NeuronFieldType.UNDEFINED, 3, 5);
        SingleGraphFragment singleFragment = bgg.generate(context);
        assertNotNull(singleFragment);
        assertTrue(!singleFragment.edges().isEmpty());

        GraphvizConsolePrinter.printGraph(context, "Bubble graph test", completeGraph);
    }

    @Test
    @DisplayName("LeafRing graph test")
    public void testLeafRing() {
        RingGraphGenerator rgg3 = new RingGraphGenerator(NeuronFieldType.UNDEFINED, 3);
        SingleGraphFragment gen3 = rgg3.generate(context);
        assertNotNull(gen3);

        GraphvizConsolePrinter.printGraph(context, "LeafRing graph test 1", completeGraph);

        LeafRingConnectorGenerator lrcg = new LeafRingConnectorGenerator(NeuronFieldType.UNDEFINED, 4, EdgeDirectionMode.FORWARD);
        GraphFragments fragments = lrcg.generate(context, gen3);
        assertNotNull(fragments);
        assertTrue(fragments.fragments().size() >= 1);

        GraphvizConsolePrinter.printGraph(context, "LeafRing graph test 2", completeGraph);
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));

        completeGraph = SingleGraphFragmentImpl.create();
        context = new GenerationContextTest();
    }

    @AfterEach
    public void afterEach() {
        completeGraph = null;
        context = null;
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
