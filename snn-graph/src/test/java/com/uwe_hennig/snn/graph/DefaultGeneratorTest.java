/**
 * @(#)DefaultGeneratorTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;

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
        public int createNode(int type) {
            return nextNode++;
        }

        @Override
        public long connect(int src, int trg) {
            return packEdge(src, trg);
        }

        @Override
        public boolean isUsed(long edgeId) {
            return bitSet.contains(edgeId);
        }

        @Override
        public void setUsed(long edgeId) {
            bitSet.add(edgeId);
        }

        private long packEdge(int srcId, int trgId) {
            long edgeId = ((long) srcId << 32) | (trgId & 0xFFFFFFFFL);
            return edgeId;
        }

    }

    @Test
    @DisplayName("Afferent test")
    public void afferentTest() {
        final int graphs = 1;
        final int nodes = 3;
        final int markUsedEdges = 2;

        GenerationContextTest context = new GenerationContextTest();
        DefaultAfferentGraphGenerator generator = new DefaultAfferentGraphGenerator(nodes, markUsedEdges);

        List<Graph> result = generator.generate(context, null);
        assertNotNull(result);
        assertEquals(graphs, result.size());
        assertEquals(nodes, result.get(0).edges().size());
        assertEquals(markUsedEdges, context.bitSet.size());
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
