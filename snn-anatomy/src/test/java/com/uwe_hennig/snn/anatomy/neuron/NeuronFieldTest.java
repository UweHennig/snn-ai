/// @(#)NeuronFieldTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/// NeuronFieldTest
///
/// @author Uwe Hennig
///
public class NeuronFieldTest {
    @Test
    @DisplayName("Simple FieldModel Test")
    public void testFieldModel() {
        final int eintries = 10;
        final int size = NeuronFieldModel.calculateSize(eintries);

        NeuronFieldModel model = new NeuronFieldModel(size);
        checkModel(model, size);

        for (int i = 0; i < eintries; i++) {
            int offset = model.claimSlot();
            try {
                model.writeLock(offset);
                model.setType(offset, i + 1);
            } catch (Exception e) {
                fail("Exception on testAsyncData " + e.getLocalizedMessage());
            } finally {
                model.writeUnlock(offset);
            }
        }

        for (int i = 0; i < eintries; i++) {
            int offset = NeuronFieldModel.getEntryOffset(i);
            try {
                assertEquals(i + 1, model.getType(offset));
            } catch (Exception e) {
                fail("Exception on testAsyncData " + e.getLocalizedMessage());
            }
        }
        model.close();
    }

    private void checkModel(NeuronFieldModel model, int totalSize) {
        System.out.printf("%nModel information");
        System.out.printf("%nTotal size     : %6d", model.totalSize);
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");

        assertEquals(totalSize, model.totalSize, "Invalid capacity!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
