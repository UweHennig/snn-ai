/**
 * @(#)FieldNodeTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * FieldNodeTest
 *
 * @author Uwe Hennig
 */
public class FieldNodeTest {

    @Test
    @DisplayName("Simple FieldGraph Out Test")
    public void testOutNeighbors() {
        MultiList multiList = new MultiList(50, 10);
        try {
            FieldNode n1 = new FieldNode(1, multiList);
            FieldNode n2 = new FieldNode(2, multiList);
            FieldNode n3 = new FieldNode(3, multiList);
            FieldNode n4 = new FieldNode(3, multiList);

            n1.addOutNeighbors(n2);
            n1.addOutNeighbors(n3);
            long[] n1Expected = { n2.getNodeId(), n3.getNodeId() };

            n2.addOutNeighbors(n4);
            long[] n2Expected = { n4.getNodeId() };

            n3.addOutNeighbors(n4);
            long[] n3Expected = { n4.getNodeId() };

            n4.addOutNeighbors(n1);
            long[] n4Expected = { n1.getNodeId() };

            printNodes("N1 Childs: ", n1.getOutNeighborsRef());
            assertArrayEquals(n1Expected, n1.getOutNeighborsRef(), "Invalid n1 childIds");

            printNodes("N2 Childs: ", n2.getOutNeighborsRef());
            assertArrayEquals(n2Expected, n2.getOutNeighborsRef(), "Invalid n2 childIds");

            printNodes("N3 Childs: ", n3.getOutNeighborsRef());
            assertArrayEquals(n3Expected, n3.getOutNeighborsRef(), "Invalid n3 childIds");

            printNodes("N4 Childs: ", n4.getOutNeighborsRef());
            assertArrayEquals(n4Expected, n4.getOutNeighborsRef(), "Invalid n4 childIds");

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

    @Test
    @DisplayName("Simple FieldGraph In Test")
    public void testInNeighbors() {
        MultiList multiList = new MultiList(50, 10);
        try {
            FieldNode n1 = new FieldNode(1, multiList);
            FieldNode n2 = new FieldNode(2, multiList);
            FieldNode n3 = new FieldNode(3, multiList);
            FieldNode n4 = new FieldNode(3, multiList);

            n1.addInNeighbors(n2);
            n1.addInNeighbors(n3);
            long[] n1Expected = { n2.getNodeId(), n3.getNodeId() };

            n2.addInNeighbors(n4);
            long[] n2Expected = { n4.getNodeId() };

            n3.addInNeighbors(n4);
            long[] n3Expected = { n4.getNodeId() };

            n4.addInNeighbors(n1);
            long[] n4Expected = { n1.getNodeId() };

            printNodes("N1 Childs: ", n1.getInNeighborsRef());
            assertArrayEquals(n1Expected, n1.getInNeighborsRef(), "Invalid n1 childIds");

            printNodes("N2 Childs: ", n2.getInNeighborsRef());
            assertArrayEquals(n2Expected, n2.getInNeighborsRef(), "Invalid n2 childIds");

            printNodes("N3 Childs: ", n3.getInNeighborsRef());
            assertArrayEquals(n3Expected, n3.getInNeighborsRef(), "Invalid n3 childIds");

            printNodes("N4 Childs: ", n4.getInNeighborsRef());
            assertArrayEquals(n4Expected, n4.getInNeighborsRef(), "Invalid n4 childIds");

            int[] count = { 0 };
            FieldNode.visit(n4, multiList, fn -> {
                System.out.println("visited: " + fn.getNodeId());
                count[0] += 1;
            });

            assertEquals(4, count[0], "Not all visited!");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception " + e.getLocalizedMessage());
        } finally {
            multiList.close();
        }
    }

    @Test
    @DisplayName("Simple FieldGraph Neuron Test")
    public void testNeurons() {
        MultiList multiList = new MultiList(50, 10);
        try {
            FieldNode node = new FieldNode(0, multiList);

            long[] expected = { 1, 2, 3, 4, 5, 6 };
            node.addNeuronId(expected);
            assertArrayEquals(expected, node.getNeuronIds());

        } finally {
            multiList.close();
        }
    }

    @Test
    @DisplayName("Simple FieldGraph Structure Test")
    public void testStructure() {
        MultiList multiList = new MultiList(50, 10);
        try {
            FieldNode n1 = new FieldNode(0, multiList);
            FieldNode n2 = new FieldNode(0, multiList);
            FieldNode n3 = new FieldNode(0, multiList);
            FieldNode n4 = new FieldNode(0, multiList);
            FieldNode n5 = new FieldNode(0, multiList);

            n3.addInNeighbors(n1);
            n3.addInNeighbors(n2);
            n3.addOutNeighbors(n4);
            n3.addOutNeighbors(n5);

            assertTrue(n1.getOutNeighborsRef().length == 1, "Invalide n1Out");
            assertTrue(n1.getInNeighborsRef().length == 0, "Invalide n1In");

            assertTrue(n2.getOutNeighborsRef().length == 1, "Invalide n2Out");
            assertTrue(n2.getInNeighborsRef().length == 0, "Invalide n2In");

            assertTrue(n3.getOutNeighborsRef().length == 2, "Invalide n3Out");
            assertTrue(n3.getInNeighborsRef().length == 2, "Invalide n3In");

            assertTrue(n4.getOutNeighborsRef().length == 0, "Invalide n4Out");
            assertTrue(n4.getInNeighborsRef().length == 1, "Invalide n4In");

            assertTrue(n5.getOutNeighborsRef().length == 0, "Invalide n5Out");
            assertTrue(n5.getInNeighborsRef().length == 1, "Invalide n5In");

        } finally {
            multiList.close();
        }
    }

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
