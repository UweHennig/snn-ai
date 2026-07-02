/**
 * @(#)SomaAllocatorTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.neuron.SomaView;

/**
 * SomaAllocatorTest
 *
 * @author Uwe Hennig
 */
public class SomaAllocatorTest {

    @Test
    @DisplayName("Simple SomaAllocator Test")
    void simpleSomaAllocatorTest() {
        SomaAllocator allocator = null;
        try {
            allocator = SomaAllocator.initInstance(100);
            SomaView view1 = allocator.newSomaView(200, 300, 400);
            assertNotNull(view1, "SomaView 1 not created!");

            SomaView view2 = allocator.newSomaView(500, 600, 700);
            assertNotNull(view1, "SomaView 2 not created!");

            assertEquals(0, view1.getViewId());
            assertEquals(200, view1.getFieldId());
            assertEquals(300, view1.getNeuronId());
            assertEquals(400, view1.getAxonId());

            assertEquals(1, view2.getViewId());
            assertEquals(500, view2.getFieldId());
            assertEquals(600, view2.getNeuronId());
            assertEquals(700, view2.getAxonId());

            SomaView view = SomaAllocator.instance().viewAt(0);
            assertEquals(0, view.getViewId());
            assertEquals(200, view.getFieldId());
            assertEquals(300, view.getNeuronId());
            assertEquals(400, view.getAxonId());

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in simpleSomaAllocatorTest" + e.getLocalizedMessage());
        } finally {
            if (allocator != null) {
                allocator.close();
            }
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
