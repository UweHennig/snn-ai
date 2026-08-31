/**
 * @(#)TapeModelTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * TapeModelTest
 *
 * @author Uwe Hennig
 */
public class TapeModelTest {

    @Test
    @DisplayName("Simple OneShotTrigger wait test")
    public void testSimpleTape() {
        int dataCapacity = 10;
        int blocks = 3;
        TapeModel model = new TapeModel(dataCapacity, blocks);

        for (int block = 0; block < blocks; block++) {
            model.setStatus(block, 0, 1);
        }

        for (int block = 0; block < blocks; block++) {
            int status = model.getStatus(block);
            assertEquals(1, status);
        }

        assertEquals(10, model.getBlockLength(0));
        int counter = 1;
        for (int block = 0; block < blocks; block++) {
            for (int data = 0; data < model.getBlockLength(0); data++) {
                model.setStimulusType(block, data, counter++);
                model.setTargetId(block, data, counter++);
                model.setTargetType(block, data, counter++);
                model.setValue(block, data, counter++);
            }
        }

        counter = 1;
        for (int block = 0; block < blocks; block++) {
            for (int data = 0; data < model.getBlockLength(0); data++) {
                int v1= model.getStimulusType(block, data);
                assertEquals(counter, v1);
                counter++;

                int v2= model.getTargetId(block, data);
                assertEquals(counter, v2);
                counter++;

                int v3= model.getTargetType(block, data);
                assertEquals(counter, v3);
                counter++;

                float v4= model.getValue(block, data);
                assertEquals(counter, v4, 0.001);
                counter++;
            }
        }

        model.close();
    }

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
