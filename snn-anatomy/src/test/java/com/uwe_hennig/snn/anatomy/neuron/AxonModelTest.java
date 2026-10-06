/// @(#)AxonModelTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/// AxonModelTest
///
/// @author Uwe Hennig
///
public class AxonModelTest {

    @Test
    @DisplayName("Simple AxonModel Test")
    public void testAxonModel() {
        int size = AxonModel.calculateSize(1);
        AxonModel model = new AxonModel(size);
        int offset = model.claimSlot(1, 2, 3);

        checkModel(model, size);

        assertEquals(1L, model.getFiedlId(offset));
        assertEquals(2L, model.getNeuronId(offset));
        assertEquals(3L, model.getBlockId(offset));

        model.close();
    }

    private void checkModel(AxonModel model, int size) {
        System.out.printf("%nModel information");
        System.out.printf("%nCapacity       : %6d", model.totalSize);
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
