/// @(#)PotentialModelTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.allocator.PotentialModelManager;

/// PotentialModelTest
///
/// @author Uwe Hennig
///
public class PotentialModelTest {

    @Test
    @DisplayName("Simple PotentialModel Test")
    public void testPotentialModel() {
        int size = PotentialModel.calculateSize(1);
        PotentialModel model = new PotentialModel(size);
        int offset = model.claimSlot();
        checkModel(model, size);

        model.setPotential(offset, 1f);
        model.setLastUpdateTime(offset, 2f);
        model.setRestingPotential(offset, 3f);
        model.setRepolarizationTime(offset, 4f);

        assertEquals(1f, model.getPotential(offset));
        assertEquals(2f, model.getLastUpdateTime(offset));
        assertEquals(3f, model.getRestingPotential(offset));
        assertEquals(4f, model.getRepolarizationTime(offset));

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
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");

        assertEquals(capacity, model.size, "Invalid size!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
