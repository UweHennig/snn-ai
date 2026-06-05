/**
 * @(#)SynapseTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * SynapseTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class SynapseTest {

    @Test
    @DisplayName("Simple SynapseModel Test")
    public void testSynapseModel() {
        SynapseModel model = new SynapseModel(1);
        checkModel(model, 1);

        try {
            model.lock(0);
            model.setFieldId(0, 1L);
            model.setNeuronId(0, 2L);
            model.setTargetId(0, 3L);
            model.setModulatorId(0, 4);
        } finally {
            model.unlock(0);
        }

        assertEquals(1L, model.getFiedlId(0));
        assertEquals(2L, model.getNeuronId(0));
        assertEquals(3L, model.getTargetId(0));
        assertEquals(4L, model.getModulatorId(0));

        model.close();
    }

    private void checkModel(SynapseModel model, int capacity) {
        System.out.println("\nData information");
        System.out.printf("%nCapacity       : %2d", model.capacity);
        System.out.printf("%nLayout size    : %2d bytes", SynapseModel.LAYOUT.byteSize());
        System.out.printf("%nByte size      : %2d bytes", model.segment.byteSize());
        System.out.printf("%nStructure      : %s%n%n", SynapseModel.LAYOUT);
        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertNotNull(model.sequenceLayout, "SequenceLayout is null!");

        assertEquals(capacity, model.capacity, "Invalid capacity!");
        assertEquals(SynapseModel.LAYOUT.byteSize() * model.capacity, model.segment.byteSize(), "Invalid segment size!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
