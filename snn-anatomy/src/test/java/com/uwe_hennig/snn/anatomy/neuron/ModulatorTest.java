/**
 * @(#)ModulatorTest.java
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
 * ModulatorTest
 * @author Uwe Hennig
 */
public class ModulatorTest {
    @Test
    @DisplayName("Simple ModulatorModel Test")
    public void testModulatorModel() {
        ModulatorModel model = null;
        try {
            model = new ModulatorModel(1);
            checkModel(model, 1);
            model.lock(0);

            model.setModulationGain(0, 1f);
            model.setGainDuration(0, 2f);
            model.setModulationGainDefault(0, 3f);
            model.setLastEventTime(0, 4f);
        } finally {
            if (model != null) {
                model.unlock(0);
            }
        }

        assertEquals(1f, model.getModulationGain(0));
        assertEquals(2f, model.getGainDuration(0));
        assertEquals(3f, model.getModulationGainDefault(0));
        assertEquals(4f, model.getLastEventTime(0));
        if (model != null) {
            model.close();
        }
    }

    private void checkModel(ModulatorModel model, int capacity) {
        System.out.printf("%nModel information");
        System.out.printf("%nCapacity       : %6d", model.capacity);
        System.out.printf("%nLayout size    : %6d bytes", ModulatorModel.LAYOUT.byteSize());
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());
        System.out.printf("%nStructure      : %s%n%n", ModulatorModel.LAYOUT);

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertNotNull(model.sequenceLayout, "SequenceLayout is null!");

        assertEquals(capacity, model.capacity, "Invalid capacity!");
        assertEquals(ModulatorModel.LAYOUT.byteSize() * model.capacity, model.segment.byteSize(), "Invalid segment size!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
