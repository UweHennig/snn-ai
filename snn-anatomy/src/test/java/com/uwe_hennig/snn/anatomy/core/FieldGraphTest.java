/**
 * @(#)FieldGraphTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * FieldGraphTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class FieldGraphTest {

    @Test
    @DisplayName("Simple FieldGraph Test")
    public void testSimple() {
        MultiList multiList = new MultiList(50, 10);
        try {
            FieldNode n1 = new FieldNode(1, multiList);
            FieldNode n2 = new FieldNode(2, multiList);
            FieldNode n3 = new FieldNode(3, multiList);
            FieldNode n4 = new FieldNode(3, multiList);

            n1.addChildNode(n2);
            n1.addChildNode(n3);
            long[] n1Expected = { n2.getNodeId(), n3.getNodeId() };

            n2.addChildNode(n4);
            long[] n2Expected = { n4.getNodeId() };

            n3.addChildNode(n4);
            long[] n3Expected = { n4.getNodeId() };

            n4.addChildNode(n1);
            long[] n4Expected = { n1.getNodeId() };

            printNodes("N1 Childs: ", n1.getChildRefs());
            assertArrayEquals(n1Expected, n1.getChildRefs(), "Invalid n1 childIds");

            printNodes("N2 Childs: ", n2.getChildRefs());
            assertArrayEquals(n2Expected, n2.getChildRefs(), "Invalid n2 childIds");

            printNodes("N3 Childs: ", n3.getChildRefs());
            assertArrayEquals(n3Expected, n3.getChildRefs(), "Invalid n3 childIds");

            printNodes("N4 Childs: ", n4.getChildRefs());
            assertArrayEquals(n4Expected, n4.getChildRefs(), "Invalid n4 childIds");

            int[] count = { 0 };
            FieldNode.visit(n4, multiList, fn -> {
                System.out.println("visited: " + fn.getNodeId());
                count[0] += 1;
            });

            assertEquals(4, count[0], "Not all visited!");

        } finally {
            multiList.close();
        }
    }

    // TODO add Parent test and more!

    private void printNodes(String info, long[] targets) {
        System.out.print(info + ": ");
        for (int i = 0; i < targets.length - 1; i++) {
            System.out.print(targets[i] + ", ");
        }
        System.out.println(targets[targets.length - 1]);
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
