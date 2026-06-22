/**
 * @(#)FieldGraphTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

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

        FieldNode node = new FieldNode(42, multiList);

        node.addParentIds(1, 2, 3);
        node.addParentIds(4, 5, 6);
        int[] expectedParentIds = { 1, 2, 3, 4, 5, 6 };

        int[] parentIds = node.getParentIds();
        printNodes("parentIds", parentIds);
        assertArrayEquals(expectedParentIds, parentIds, "Invalid parentIds");

        node.addChildIds(7, 8, 9);
        node.addChildIds(10, 11, 12);
        int[] expectedChildIds = { 7, 8, 9, 10, 11, 12 };

        int[] childIds = node.getChildIds();
        printNodes("childIds", childIds);
        assertArrayEquals(expectedChildIds, childIds, "Invalid childIds");

        node.addNeuronIds(13, 14, 15);
        node.addNeuronIds(16, 17, 18);
        int[] expectedNeuronIds = { 13, 14, 15, 16, 17, 18 };

        int[] neuronIds = node.getNeuronIds();
        printNodes("neuronIds", neuronIds);
        assertArrayEquals(expectedNeuronIds, neuronIds, "Invalid neuronIds");

        multiList.close();
    }

    private void printNodes(String info, int[] targets) {
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
