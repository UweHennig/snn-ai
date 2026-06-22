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
        MultiList multiList = new MultiList(100, 50);

        FieldNode node = new FieldNode(42, multiList);

        node.addParentIds(1, 2, 3);
        node.addParentIds(4, 5, 6);

        int [] parentIds = node.getParentIds();
        printNodes("parentIds", parentIds);

        int [] expected = {1, 2, 3, 4, 5, 6};
        assertArrayEquals(expected, parentIds, "Invalid parentIds");
    }

    private void printNodes(String info, int [] targets) {
        System.out.print(info + ": ");
        for(int i=0;i<targets.length-1;i++) {
            System.out.print(targets[i] + ", ");
        }
        System.out.println(targets[targets.length-1]);
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
