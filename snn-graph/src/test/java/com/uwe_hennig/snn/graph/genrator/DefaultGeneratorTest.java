/**
 * @(#)DefaultGeneratorTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.genrator;

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
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.graph.SingleGraphFragmentImpl;

/**
 * DefaultGeneratorTest
 *
 * @author Uwe Hennig
 */
public class DefaultGeneratorTest {
    public class GenerationContextTest implements GenerationContext {
        private SingleGraphFragment completeGraph = SingleGraphFragmentImpl.create();
        private int                 nextNode      = 0;
        private long                edgeId        = 0;
        public HashSet<Long>        bitSet        = new HashSet<>();

        @Override
        public boolean isEdgeMarked(long edgeId) {
            return bitSet.contains(edgeId);
        }

        @Override
        public void markEdge(long edgeId) {
            bitSet.add(edgeId);
        }

        @Override
        public void unmarkEdge(long edgeId) {
            bitSet.remove(edgeId);
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
            return edgeId++;
        }

        @Override
        public SingleGraphFragment completeGraph() {
            return completeGraph;
        }

        @Override
        public int nodeCount() {
            return nextNode;
        }
    }

    @Test
    @DisplayName("Afferent graph test")
    public void testDefaultAfferent() {
        // TODO
    }

    @Test
    @DisplayName("Associative graph test")
    public void testDefaultAssociative() {
        // TODO
    }

    @Test
    @DisplayName("Efferent graph test")
    public void testDefaultEffernt() {
        // TODO
    }

    @Test
    @DisplayName("Feedback graph test")
    public void testDefaultFeedback() {
        // TODO
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterEach
    public void afterEach() {
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
