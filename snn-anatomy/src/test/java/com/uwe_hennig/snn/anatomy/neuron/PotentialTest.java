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

/**
 * PotentialTest
 *
 * @author Uwe Hennig
 */
public class PotentialTest {

    @Test
    @DisplayName("Simple PotentialModel Test")
    public void testPotentialModel() {
        PotentialModel model = new PotentialModel(1);
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

        model.close();
    }

    @Test
    @DisplayName("Simple PotentialView Test")
    public void testPotentialView() {
        final int n = 10;
        PotentialModel model = null;
        try {
            model = new PotentialModel(n);
            checkModel(model, n);
            float currentTime = 3.1415f;

            PotentialView[] views = new PotentialView[n];
            for (int i = 0; i < n; i++) {
                views[i] = new PotentialView(i, model);
                model.writeLock(i);
                model.setPotential(i, 0f);
                model.setRestingPotential(i, 0f);
                model.writeUnlock(i);
            }

            for (int i = 0; i < n; i++) {
                PotentialView view = views[i];
                assertEquals(0f, view.getPotentital());
                assertEquals(0f, view.getRestingPotential());
            }

            float c = 0f;
            for (int i = 0; i < n; i++) {
                PotentialView view = views[i];
                c += 1f;
                view.addPotentitial(c, currentTime);
            }

            c = 0f;
            for (int i = 0; i < n; i++) {
                PotentialView view = views[i];
                c += 1f;
                float currentPotenial = view.getPotentital();

                assertEquals(c, currentPotenial);
                view.withActionPotential(1000f, currentTime);
                assertTrue(view.getPotentital() >= model.getRestingPotential(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("Eception in testPotentialView " + e.getLocalizedMessage());
        } finally {
            if (model != null) {
                model.close();
            }
        }
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
