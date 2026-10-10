/// @(#)SynapseModelTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/// SynapseModelTest
///
/// @author Uwe Hennig
///
public class SynapseModelTest {

    @Test
    @DisplayName("Simple SynapseModel Test")
    public void testSynapseModel() {
        int size = SynapseModel.calculateSize(1);
        SynapseModel model = new SynapseModel(size);

        int offset = 0;

        offset = model.claimSlot(1, 2, 3);
        model.writeLock(offset);
        model.setModulatorId(offset, 4);
        model.setTargetType(offset, 5);
        checkModel(model, size);

        assertEquals(1L, model.getFiedlId(offset));
        assertEquals(2L, model.getNeuronId(offset));
        assertEquals(3L, model.getTargetId(offset));

        assertEquals(4L, model.getModulatorId(offset));
        assertEquals(5L, model.getTargetType(offset));

        model.close();
    }

    private void checkModel(SynapseModel model, int capacity) {
        System.out.println("\nData information");
        System.out.printf("%nCapacity       : %2d", model.totalSize);
        System.out.printf("%nByte size      : %2d bytes", model.segment.byteSize());
        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");

        assertEquals(capacity, model.totalSize, "Invalid capacity!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
