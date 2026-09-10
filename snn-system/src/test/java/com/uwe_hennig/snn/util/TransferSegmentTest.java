/**
 * @(#)TransferSegmentTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * TransferSegmentTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class TransferSegmentTest {

    @Test
    @DisplayName("TransferSegment Data Test")
    public void dataTest() {
        TransferSegment ts = new TransferSegment(2048);

        // --- meta test
        int value;
        value = ts.getNumBlocks();
        assertEquals(0, value, "invalid mum blocks");

        value = ts.getStartOffset();
        assertEquals(20, value, "invalid start offset");

        value = ts.getHeaderSize();
        assertEquals(20, value, "invalid header size");

        value = ts.getEntrySize();
        assertEquals(12, value, "invalid entry size");

        value = ts.getEndOffset();
        assertEquals(40, value, "invalid end offset");

        // --- block test
        int blockOffset = ts.allocateBlock(2);
        assertEquals(40, blockOffset, "invalid allocate block");

        value = ts.getEndOffset();
        assertEquals(64, value, "invalid end offset");

        ts.setState(blockOffset, 1);
        ts.setCount(blockOffset, 2);
        ts.setCapacity(blockOffset, 3);
        ts.setStimulusType(blockOffset, 4);
        ts.setNextBlock(blockOffset, 5);

        value = ts.getState(blockOffset);
        assertEquals(1, value, "invalid state");

        value = ts.getCount(blockOffset);
        assertEquals(2, value, "invalid count");

        value = ts.getCapacity(blockOffset);
        assertEquals(3, value, "invalid capacity");

        value = ts.getStimulusType(blockOffset);
        assertEquals(4, value, "invalid stimulus type");

        value = ts.getNextBlock(blockOffset);
        assertEquals(5, value, "invalid next block");

        // --- entry test
        ts.setEntry(blockOffset, 0, 6, 7, 8.0f);
        ts.setEntry(blockOffset, 1, 9, 10, 11.0f);

        value = ts.getTargetId(blockOffset, 0);
        assertEquals(6, value, "invalid target id at 0");

        value = ts.getTargetId(blockOffset, 1);
        assertEquals(9, value, "invalid target id at 1");

        value = ts.getTargetType(blockOffset, 0);
        assertEquals(7, value, "invalid target type at 0");

        value = ts.getTargetType(blockOffset, 1);
        assertEquals(10, value, "invalid target tpye at 0");

        float fValue = ts.getValue(blockOffset, 0);
        assertEquals(8f, fValue, "invalid value at 0");

        fValue = ts.getValue(blockOffset, 1);
        assertEquals(11f, fValue, "invalid value at 1");

        ts.close();
    }

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
