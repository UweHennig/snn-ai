/// @(#)PlasticityModelTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/// PlasticityModelTest
///
/// @author Uwe Hennig
///
public class PlasticityModelTest {
    @Test
    @DisplayName("Simple PlasticityModel Test")
    public void testPlasticityModel() {
        int size = PlasticityModel.calculateSize(1);
        PlasticityModel model = new PlasticityModel(size);
        int offset = model.claimSlot();

        model.setCurrentPotential(offset, 1f);
        model.setLastUpdateTime(offset, 2f);
        model.setRestingPotential(offset, 3f);
        model.setRestingRate(offset, 4f);
        model.setRestingTime(offset, 5f);
        model.setTargetPotential(offset, 6f);
        model.setTargetRate(offset, 7f);
        model.setTargetTime(offset, 8f);

        checkNaN(model, offset);

        assertEquals(1f, model.getCurrentPotential(offset));
        assertEquals(2f, model.getLastUpdateTime(offset));
        assertEquals(3f, model.getRestingPotential(offset));
        assertEquals(4f, model.getRestingRate(offset));
        assertEquals(5f, model.getRestingTime(offset));
        assertEquals(6f, model.getTargetPotential(offset));
        assertEquals(7f, model.getTargetRate(offset));
        assertEquals(8f, model.getTargetTime(offset));
    }

    @Test
    @DisplayName("Simple PlasticityModel Test")
    public void testPlasticityView() {
        //        PlasticityModel model = PlasticityModelManager.init(1).getModel();
        //        PlasticityView plasticityView = PlasticityModelManager.instance().createView();
        //        try {
        //            // initial values
        //            float startTime = 200f;
        //            model.setCurrentPotential(0, -40f);
        //            model.setLastUpdateTime(0, startTime);
        //
        //            model.setTargetPotential(0, 60f);
        //            model.setRestingPotential(0, -40);
        //
        //            model.setTargetTime(0, 1000f);
        //            model.setRestingTime(0, 1000f);
        //
        //            model.setTargetRate(0, 2f);
        //            model.setRestingRate(0, 2f);
        //
        //            System.out.println("### Initial values");
        //            System.out.println("currentPotential = " + model.getCurrentPotential(0));
        //            System.out.println("lastUpdateTime = " + model.getLastUpdateTime(0));
        //
        //            System.out.println("targetPotential = " + model.getTargetPotential(0));
        //            System.out.println("restingPotential = " + model.getRestingPotential(0));
        //
        //            System.out.println("targetTime = " + model.getTargetTime(0));
        //            System.out.println("restingTime = " + model.getRestingTime(0));
        //
        //            System.out.println("targetRate = " + model.getTargetRate(0));
        //            System.out.println("restingRate = " + model.getRestingRate(0));
        //            checkNaN(model);
        //
        //            float currentTime = 400f;
        //
        //            plasticityView.updatePlasticityPotential(currentTime + 100);
        //            plasticityView.applyValueFeedback(100, currentTime + 200);
        //            plasticityView.applyTimeFeedback(100, currentTime + 300);
        //
        //            System.out.println("\n### Changed values");
        //            System.out.println("elapsed = " + (currentTime - model.getLastUpdateTime(0)));
        //            System.out.println("currentPotential = " + model.getCurrentPotential(0));
        //            System.out.println("lastUpdateTime = " + model.getLastUpdateTime(0));
        //
        //            System.out.println("targetPotential = " + model.getTargetPotential(0));
        //            System.out.println("restingPotential = " + model.getRestingPotential(0));
        //
        //            System.out.println("targetTime = " + model.getTargetTime(0));
        //            System.out.println("restingTime = " + model.getRestingTime(0));
        //
        //            System.out.println("targetRate = " + model.getTargetRate(0));
        //            System.out.println("restingRate = " + model.getRestingRate(0));
        //            checkNaN(model);
        //
        //            assertTrue(model.getCurrentPotential(0) > -40f, "invalid currentPotential");
        //            assertTrue(model.getLastUpdateTime(0) == 700f, "invalid lastUpdateTime");
        //            assertTrue(model.getTargetPotential(0) > -50f && model.getTargetPotential(0) < 90f, "invalid targetPotential");
        //            assertTrue(model.getRestingPotential(0) >= -90f && model.getRestingPotential(0) <= -40f, "invalid restingPotential");
        //
        //            // no targetTime changes implemented!
        //
        //            assertTrue(model.getTargetRate(0) >= 2 && model.getTargetRate(0) <= 5, "invalid targetRate");
        //            assertTrue(model.getRestingRate(0) >= 2 && model.getRestingRate(0) <= 5, "invalid restingRate");
        //
        //        } finally {
        //            PlasticityModelManager.close();
        //        }
        //
    }

    private void checkNaN(PlasticityModel model, int offset) {
        assertFalse(Float.isNaN(model.getCurrentPotential(offset)));

        assertFalse(Float.isNaN(model.getLastUpdateTime(offset)), "NaN Error getLastUpdateTime");
        assertFalse(Float.isNaN(model.getTargetPotential(offset)), "NaN Error getTargetPotential");
        assertFalse(Float.isNaN(model.getRestingPotential(offset)), "NaN Error getRestingPotential");
        assertFalse(Float.isNaN(model.getTargetTime(offset)), "NaN Error getTargetTime");
        assertFalse(Float.isNaN(model.getRestingTime(offset)), "NaN Error getRestingTime");
        assertFalse(Float.isNaN(model.getTargetRate(offset)), "NaN Error getTargetRate");
        assertFalse(Float.isNaN(model.getRestingRate(offset)), "NaN Error getRestingRate");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
