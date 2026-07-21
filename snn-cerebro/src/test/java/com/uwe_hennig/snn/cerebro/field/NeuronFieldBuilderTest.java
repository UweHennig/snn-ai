/**
 * @(#)NeuronFieldBuilderTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * NeuronFieldBuilderTest
 *
 * @author Uwe Hennig
 */
public class NeuronFieldBuilderTest {

    @Test
    @DisplayName("Simple NeuronField pipeline Test")
    public void testSimplePipeline() {
    }

    @Test
    @DisplayName("Simple NeuronField branching Test")
    public void testBranching() {
    }

    @Test
    @DisplayName("Simple NeuronField feedback type Test")
    public void testFeedbackField() {
    }

    @Test
    @DisplayName("Simple NeuronField nested structure Test")
    public void testNestedStructure() {
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
//        NeuronFieldModelManager.init(10);
//        NeuronFieldListManager.init(100, 100);

        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterEach
    public void clearEach() {
//        if (NeuronFieldModelManager.instance() != null) {
//            NeuronFieldModelManager.instance();
//            NeuronFieldModelManager.close();
//        }
    }
}
