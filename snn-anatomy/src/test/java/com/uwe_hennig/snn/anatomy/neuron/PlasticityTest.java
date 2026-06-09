/**
 * @(#)PlasticityTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * PlasticityTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class PlasticityTest {
    @Test
    @DisplayName("Simple PlasticityModel Test")
    public void testPlasticityModel() {
        PlasticityModel model = null;
        try {
            model = new PlasticityModel(1);
            model.lock(0);
            model.setCurrentPotential(0, 1f);
            model.setLastUpdateTime(0, 2f);
            model.setRestingPotential(0, 3f);
            model.setRestingRate(0, 4f);
            model.setRestingTime(0, 5f);
            model.setTargetPotential(0, 6f);
            model.setTargetRate(0, 7f);
            model.setTargetTime(0, 8f);
            model.unlock(0);

            assertEquals(1f, model.getCurrentPotential(0));
            assertEquals(2f, model.getLastUpdateTime(0));
            assertEquals(3f, model.getRestingPotential(0));
            assertEquals(4f, model.getRestingRate(0));
            assertEquals(5f, model.getRestingTime(0));
            assertEquals(6f, model.getTargetPotential(0));
            assertEquals(7f, model.getTargetRate(0));
            assertEquals(8f, model.getTargetTime(0));
        } finally {
            model.close();
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
