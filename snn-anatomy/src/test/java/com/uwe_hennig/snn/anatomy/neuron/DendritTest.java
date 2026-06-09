/**
 * @(#)DendritTest.java
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
 * DendritTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class DendritTest {

    @Test
    @DisplayName("Simple DendritModel Test")
    public void testDendritModel() {
        DendritModel model = new DendritModel(1);
        checkModel(model, 1);

        assertEquals(24, model.sequenceLayout.byteSize());
        assertEquals(1, model.capacity);

        try {
            model.lock(0);
            model.setFieldId(0, 1);
            model.setNeuronId(0, 2);
            model.setSomaId(0, 3);
            model.setWeightId(0, 4);
        } finally {
            model.unlock(0);
        }

        assertEquals(1, model.getFiedlId(0));
        assertEquals(2, model.getNeuronId(0));
        assertEquals(3, model.getSomaId(0));
        assertEquals(4, model.getWeighId(0));

        model.close();
    }

    private void checkModel(DendritModel model, int capacity) {
        System.out.println("\nData information");
        System.out.printf("%nCapacity       : %2d", model.capacity);
        System.out.printf("%nLayout size    : %2d bytes", DendritModel.LAYOUT.byteSize());
        System.out.printf("%nByte size      : %2d bytes", model.segment.byteSize());
        System.out.printf("%nStructure      : %s%n%n", DendritModel.LAYOUT);
        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertNotNull(model.sequenceLayout, "SequenceLayout is null!");

        assertEquals(capacity, model.capacity, "Invalid capacity!");
        assertEquals(DendritModel.LAYOUT.byteSize() * model.capacity, model.segment.byteSize(), "Invalid segment size!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
