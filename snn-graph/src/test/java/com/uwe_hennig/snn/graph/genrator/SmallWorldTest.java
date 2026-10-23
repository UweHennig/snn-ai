/// @(#)SmallWorldTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.graph.genrator;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.graph.generator.SmallWorld;
import com.uwe_hennig.snn.graph.generator.contracts.Node;
import com.uwe_hennig.snn.graph.util.GraphvizPrinter;

/// SmallWorldTest
///
/// @author Uwe Hennig
public class SmallWorldTest {
    private final int NOT_REQUIRED = -1;
    private final Set<Node> EMPTY_SET = new LinkedHashSet<>();

    @Test
    public void testSimpleWorldGraph() {
        try {
            Set<Node> smallWorld = SmallWorld.generateGraph(55, NOT_REQUIRED, NOT_REQUIRED, 10, 2, 0.3f);
            assertNotNull(smallWorld, "SmalWorld returns null!");
            assertFalse(smallWorld.isEmpty(), "No samall world data generated!");
            for (Node node: smallWorld) {
                assertFalse(node.inNeighbours().isEmpty(), "The 'in' set should not be empty");
                assertFalse(node.outNeighbours().isEmpty(), "The 'out' set should not be empty");
            }

            String dot= GraphvizPrinter.toDot("Test SmallWorld", smallWorld, EMPTY_SET, EMPTY_SET, EMPTY_SET);
            System.out.println(dot);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in testSimpleWorldGraph : " + e.getLocalizedMessage());
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
