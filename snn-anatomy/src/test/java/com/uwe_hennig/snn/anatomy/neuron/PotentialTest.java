/**
 * @(#)PotentialTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.allocator.PotentialModelManager;

/**
 * PotentialTest
 *
 * @author Uwe Hennig
 */
public class PotentialTest {

    @Test
    @DisplayName("Simple PotentialModel Test")
    public void testPotentialModel() {
        PotentialModel model = PotentialModelManager.init(1).getModel();
        checkModel(model, 1);

        try {
            model.writeLock(0);
            model.setPotential(0, 1f);
            model.setLastUpdateTime(0, 2f);
            model.setRestingPotential(0, 3f);
            model.setRepolarizationTime(0, 4f);
        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception in testPotentialModel " + e.getLocalizedMessage());
        } finally {
            model.writeUnlock(0);
        }

        assertEquals(1f, model.getPotential(0));
        assertEquals(2f, model.getLastUpdateTime(0));
        assertEquals(3f, model.getRestingPotential(0));
        assertEquals(4f, model.getRepolarizationTime(0));

        PotentialModelManager.close();
    }

    @Test
    @DisplayName("Simple PotentialView Test")
    public void testPotentialView() {
// TODO
//        final int n = 10;
//        PotentialModel model = PotentialModelManager.init(n).getModel();
//        try {
//            checkModel(model, n);
//            float currentTime = 3.1415f;
//
//            for (int i = 0; i < n; i++) {
//                model.writeLock(i);
//                model.setPotential(i, 0f);
//                model.writeUnlock(i);
//            }
//
//            for (int i = 0; i < n; i++) {
//                assertEquals(0f, model.getPotential(i));
//            }
//
//            float c = 0f;
//            for (int i = 0; i < n; i++) {
//                c += 1f;
//                PotentialView.addPotentitial(i, c, currentTime);
//            }
//
//            c = 0f;
//            for (int i = 0; i < n; i++) {
//                c += 1f;
//                float currentPotenial = PotentialView.getPotential(i);
//
//                assertEquals(c, currentPotenial);
//                PotentialView.addPotentitial(i, 1000f, currentTime);
//                assertTrue(PotentialView.getPotential(i) >= model.getRestingPotential(i));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            fail("Eception in testPotentialView " + e.getLocalizedMessage());
//        } finally {
//            PotentialModelManager.close();
//        }
    }

    private void checkModel(PotentialModel model, int capacity) {
        System.out.printf("%nModel information");
        System.out.printf("%nCapacity       : %6d", model.capacity);
        System.out.printf("%nLayout size    : %6d bytes", PotentialModel.LAYOUT.byteSize());
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());
        System.out.printf("%nStructure      : %s%n%n", PotentialModel.LAYOUT);

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertNotNull(model.sequenceLayout, "SequenceLayout is null!");

        assertEquals(capacity, model.capacity, "Invalid capacity!");
        assertEquals(PotentialModel.LAYOUT.byteSize() * model.capacity, model.segment.byteSize(), "Invalid segment size!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
