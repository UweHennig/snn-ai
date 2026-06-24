/**
 * @(#)FieldTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * FieldTest
 * @author Uwe Hennig
 */
public class FieldTest {
    @Test
    @DisplayName("Simple FieldModel Test")
    public void testFieldModel() {
        final int capacity = 10;
        FieldModel model = new FieldModel(capacity);
        checkModel(model, capacity);

        for (int i = 0; i < capacity; i++) {
            try {
                model.writeLock(i);
                model.setType(i, i + 1);
                model.setLevel(i, i + 2);

            } catch (Exception e) {
                fail("Exception on testAsyncData " + e.getLocalizedMessage());
            } finally {
                model.writeUnlock(i);
            }
        }
        for (int i = 0; i < capacity; i++) {
            try {
                assertEquals(i + 1, model.getType(i));
                assertEquals(i + 2, model.getLevel(i));
            } catch (Exception e) {
                fail("Exception on testAsyncData " + e.getLocalizedMessage());
            }
        }
        model.close();
    }

    private void checkModel(FieldModel model, int capacity) {
        System.out.printf("%nModel information");
        System.out.printf("%nCapacity       : %6d", model.capacity);
        System.out.printf("%nLayout size    : %6d bytes", FieldModel.LAYOUT.byteSize());
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());
        System.out.printf("%nStructure      : %s%n%n", FieldModel.LAYOUT);

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertNotNull(model.sequenceLayout, "SequenceLayout is null!");

        assertEquals(capacity, model.capacity, "Invalid capacity!");
        assertEquals(FieldModel.LAYOUT.byteSize() * model.capacity, model.segment.byteSize(), "Invalid segment size!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
