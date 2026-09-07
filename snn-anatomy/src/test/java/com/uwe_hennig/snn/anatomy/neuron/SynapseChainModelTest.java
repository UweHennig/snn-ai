/**
 * @(#)SynapseChainModelTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * SynapseChainModelTest
 *
 * @author Uwe Hennig
 */
public class SynapseChainModelTest {

    @Test
    public void testSingleBlock() {
        SynapseChainModel list = new SynapseChainModel(1024);

        int offset = list.allocate(4);

        list.addSynapseId(offset, 10);
        list.addSynapseId(offset, 20);
        list.addSynapseId(offset, 30);

        int[] syn = list.getSynapses(offset);

        assertArrayEquals(new int[]{10, 20, 30}, syn);
    }

    @Test
    public void testTwoBlocks() {
        SynapseChainModel list = new SynapseChainModel(2048);

        int offset = list.allocate(2);

        list.addSynapseId(offset, 1);
        list.addSynapseId(offset, 2);
        list.addSynapseId(offset, 3); // triggers recursion

        int[] syn = list.getSynapses(offset);

        assertArrayEquals(new int[]{1, 2, 3}, syn);
    }


    @Test
    public void testMultiBlocks() {
        SynapseChainModel list = new SynapseChainModel(4096);

        int offset = list.allocate(3);

        for (int i = 0; i < 10; i++) {
            list.addSynapseId(offset, i);
        }

        int[] syn = list.getSynapses(offset);

        assertArrayEquals(new int[]{0,1,2,3,4,5,6,7,8,9}, syn);
    }

    @Test
    public void testNextBlockTerminator() {
        SynapseChainModel list = new SynapseChainModel(1024);

        int offset = list.allocate(1);

        list.addSynapseId(offset, 42);

        assertEquals(-1, list.getNextBlock(offset));
    }

    @Test
    public void testHeaderIntegrity() {
        SynapseChainModel list = new SynapseChainModel(2048);

        int offset = list.allocate(2);

        list.addSynapseId(offset, 100);
        list.addSynapseId(offset, 200);
        list.addSynapseId(offset, 300);

        assertEquals(2, list.getBlockCapacity(offset));
        assertEquals(2, list.getNumElements(offset));
        assertEquals(3, list.getTotalElements(offset));

        int next = list.getNextBlock(offset);

        assertEquals(1, list.getNumElements(next));
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
