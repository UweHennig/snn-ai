/**
 * @(#)AxonTest.java
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
 * AxonTest
 * @author Uwe Hennig
 */
public class AxonTest {

    @Test
    @DisplayName("Simple AxonModel Test")
    public void testAxonModel() {
        AxonModel model = new AxonModel(1);
        checkModel(model, 1);

        try {
            model.writeLock(0);
            model.setFieldId(0, 1);
            model.setNeuronId(0, 2);
            model.setModulatorId(0, 3);
            model.setSynapseRef(0, 4);
        } finally {
            model.writeUnlock(0);
        }

        assertEquals(1L, model.getFiedlId(0));
        assertEquals(2L, model.getNeuronId(0));
        assertEquals(3L, model.getModulatorId(0));
        assertEquals(4L, model.getSynapseRef(0));

        model.close();
    }

    private void checkModel(AxonModel model, int capacity) {
        System.out.printf("%nModel information");
        System.out.printf("%nCapacity       : %6d", model.capacity);
        System.out.printf("%nLayout size    : %6d bytes", AxonModel.LAYOUT.byteSize());
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());
        System.out.printf("%nStructure      : %s%n%n", AxonModel.LAYOUT);

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertNotNull(model.sequenceLayout, "SequenceLayout is null!");

        assertEquals(capacity, model.capacity, "Invalid capacity!");
        assertEquals(AxonModel.LAYOUT.byteSize() * model.capacity, model.segment.byteSize(), "Invalid segment size!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
