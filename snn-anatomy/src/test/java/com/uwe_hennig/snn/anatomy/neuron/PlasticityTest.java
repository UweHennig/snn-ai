/**
 * @(#)PlasticityTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.allocator.PlasticityModelManager;

/**
 * PlasticityTest
 *
 * @author Uwe Hennig
 */
public class PlasticityTest {
    @Test
    @DisplayName("Simple PlasticityModel Test")
    public void testPlasticityModel() {
        PlasticityModel model = PlasticityModelManager.init(1).getModel();
        try {
            model.writeLock(0);
            model.setCurrentPotential(0, 1f);
            model.setLastUpdateTime(0, 2f);
            model.setRestingPotential(0, 3f);
            model.setRestingRate(0, 4f);
            model.setRestingTime(0, 5f);
            model.setTargetPotential(0, 6f);
            model.setTargetRate(0, 7f);
            model.setTargetTime(0, 8f);
            model.writeUnlock(0);

            checkNaN(model);

            assertEquals(1f, model.getCurrentPotential(0));
            assertEquals(2f, model.getLastUpdateTime(0));
            assertEquals(3f, model.getRestingPotential(0));
            assertEquals(4f, model.getRestingRate(0));
            assertEquals(5f, model.getRestingTime(0));
            assertEquals(6f, model.getTargetPotential(0));
            assertEquals(7f, model.getTargetRate(0));
            assertEquals(8f, model.getTargetTime(0));
        } finally {
            if (model != null) {
                model.close();
            }
        }
    }

    @Test
    @DisplayName("Simple PlasticityModel Test")
    public void testPlasticityView() {
        PlasticityModel model = PlasticityModelManager.init(1).getModel();
        PlasticityView view = null;
        try {
            // initial values
            model.writeLock(0);
            float startTime = 200f;
            model.setCurrentPotential(0, -40f);
            model.setLastUpdateTime(0, startTime);

            model.setTargetPotential(0, 60f);
            model.setRestingPotential(0, -40);

            model.setTargetTime(0, 1000f);
            model.setRestingTime(0, 1000f);

            model.setTargetRate(0, 2f);
            model.setRestingRate(0, 2f);
            model.writeUnlock(0);

            System.out.println("### Initial values");
            System.out.println("currentPotential = " + model.getCurrentPotential(0));
            System.out.println("lastUpdateTime = " + model.getLastUpdateTime(0));

            System.out.println("targetPotential = " + model.getTargetPotential(0));
            System.out.println("restingPotential = " + model.getRestingPotential(0));

            System.out.println("targetTime = " + model.getTargetTime(0));
            System.out.println("restingTime = " + model.getRestingTime(0));

            System.out.println("targetRate = " + model.getTargetRate(0));
            System.out.println("restingRate = " + model.getRestingRate(0));
            checkNaN(model);

            float currentTime = 400f;

            PlasticityView.updatePlasticityPotential(0, currentTime + 100);
            PlasticityView.applyValueFeedback(0, 100, currentTime + 200);
            PlasticityView.applyTimeFeedback(0, 100, currentTime + 300);

            System.out.println("\n### Changed values");
            System.out.println("elapsed = " + (currentTime - model.getLastUpdateTime(0)));
            System.out.println("currentPotential = " + model.getCurrentPotential(0));
            System.out.println("lastUpdateTime = " + model.getLastUpdateTime(0));

            System.out.println("targetPotential = " + model.getTargetPotential(0));
            System.out.println("restingPotential = " + model.getRestingPotential(0));

            System.out.println("targetTime = " + model.getTargetTime(0));
            System.out.println("restingTime = " + model.getRestingTime(0));

            System.out.println("targetRate = " + model.getTargetRate(0));
            System.out.println("restingRate = " + model.getRestingRate(0));
            checkNaN(model);

            assertTrue(model.getCurrentPotential(0) > -40f, "invalid currentPotential");
            assertTrue(model.getLastUpdateTime(0) == 700f, "invalid lastUpdateTime");
            assertTrue(model.getTargetPotential(0) > -50f && model.getTargetPotential(0) < 90f, "invalid targetPotential");
            assertTrue(model.getRestingPotential(0) >= -90f && model.getRestingPotential(0) <= -40f, "invalid restingPotential");

            // no targetTime changes implemented!

            assertTrue(model.getTargetRate(0) >= 2 && model.getTargetRate(0) <= 5, "invalid targetRate");
            assertTrue(model.getRestingRate(0) >= 2 && model.getRestingRate(0) <= 5, "invalid restingRate");

        } finally {
            if (model != null) {
                model.close();
            }
        }

    }

    private void checkNaN(PlasticityModel model) {
        assertFalse(Float.isNaN(model.getCurrentPotential(0)));

        assertFalse(Float.isNaN(model.getLastUpdateTime(0)), "NaN Error getLastUpdateTime");
        assertFalse(Float.isNaN(model.getTargetPotential(0)), "NaN Error getTargetPotential");
        assertFalse(Float.isNaN(model.getRestingPotential(0)), "NaN Error getRestingPotential");
        assertFalse(Float.isNaN(model.getTargetTime(0)), "NaN Error getTargetTime");
        assertFalse(Float.isNaN(model.getRestingTime(0)), "NaN Error getRestingTime");
        assertFalse(Float.isNaN(model.getTargetRate(0)), "NaN Error getTargetRate");
        assertFalse(Float.isNaN(model.getRestingRate(0)), "NaN Error getRestingRate");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
