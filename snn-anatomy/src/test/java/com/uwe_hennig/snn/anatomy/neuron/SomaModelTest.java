/// @(#)SomaModelTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/// SomaModelTest
///
/// @author Uwe Hennig
///
public class SomaModelTest {

    @Test
    @DisplayName("Simple SomaModel Test")
    public void testSomaModel() {
        int size = SomaModel.calculateSize(1);
        SomaModel model = new SomaModel(size);
        checkModel(model, size);
        int offset = model.claimSlot();

        try {
            model.writeLock(offset);
            model.setFieldId(offset, 1);
            model.setNeuronId(offset, 2);
            model.setPotentialId(offset, 3);
            model.setThresholdId(offset, 4);
            model.setStpId(offset, 5);
            model.setLtpId(offset, 6);
        } finally {
            model.writeUnlock(offset);
        }

        assertEquals(1L, model.getFieldId(offset));
        assertEquals(2L, model.getNeuronId(offset));
        assertEquals(3L, model.getPotentialId(offset));
        assertEquals(4L, model.getThresholdId(offset));
        assertEquals(5L, model.getStpId(offset));
        assertEquals(6L, model.getLtpId(offset));

        model.close();
    }

    private void checkModel(SomaModel model, int size) {
        System.out.printf("%nModel information");
        System.out.printf("%nTotalSize      : %6d", model.totalSize);
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");

        assertEquals(size, model.totalSize, "Invalid capacity!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
