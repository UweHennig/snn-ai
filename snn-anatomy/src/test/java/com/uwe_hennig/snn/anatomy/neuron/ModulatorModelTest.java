/// @(#)ModulatorModelTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/// ModulatorModelTest
///
/// @author Uwe Hennig
///
public class ModulatorModelTest {
    @Test
    @DisplayName("Simple ModulatorModel Test")
    public void testModulatorModel() {
        int size = ModulatorModel.calculateSize(1);
        ModulatorModel model = new ModulatorModel(size);
        checkModel(model, size);
        int offset = model.claimSlot();

        model.setModulationGain(offset, 1f);
        model.setGainDuration(offset, 2f);
        model.setModulationGainDefault(offset, 3f);
        model.setLastEventTime(offset, 4f);

        assertEquals(1f, model.getModulationGain(offset));
        assertEquals(2f, model.getGainDuration(offset));
        assertEquals(3f, model.getModulationGainDefault(offset));
        assertEquals(4f, model.getLastEventTime(offset));

        model.close();
    }

    private void checkModel(ModulatorModel model, int size) {
        System.out.printf("%nModel information");
        System.out.printf("%nCapacity       : %6d", model.size);
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");

        assertEquals(size, model.size, "Invalid capacity!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
