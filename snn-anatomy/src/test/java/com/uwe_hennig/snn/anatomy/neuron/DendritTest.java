///  @(#)DendritTest.java
///  Copyright (c) 2026 Uwe Hennig
//   All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

///  DendritTest
///
///  @author Uwe Hennig
public class DendritTest {

    @Test
    @DisplayName("Simple DendritModel Test")
    public void testDendritModel() {
        final int capacity = 48;
        DendritModel model = new DendritModel(capacity);
        checkModel(model, capacity);
        assertEquals(capacity, model.capacity);
        int offset = model.alloc(1, 2, 3, 4);

        try {
            model.writeLock(offset);

            model.setFieldId(offset, 1);
            model.setNeuronId(offset, 2);
            model.setSomaId(offset, 3);
            model.setWeightId(offset, 4);
        } finally {
            model.writeUnlock(offset);
        }

        try {
            model.readLock(offset);

            assertEquals(1, model.getFiedlId(offset));
            assertEquals(2, model.getNeuronId(offset));
            assertEquals(3, model.getSomaId(offset));
            assertEquals(4, model.getWeighId(offset));
        } finally {
            model.readUnlock(offset);
        }

        model.close();
    }

    private void checkModel(DendritModel model, int capacity) {
        System.out.println("\nData information");
        System.out.printf("%nCapacity   : %2d", model.capacity);
        System.out.printf("%nEntry size : %2d bytes", model.getEntrySize());

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");

        assertEquals(capacity, model.capacity, "Invalid capacity!");
        assertEquals(capacity, model.getCapacity(), "Invalid capacity!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
