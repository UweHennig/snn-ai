/**
 * @(#)NeuronFieldBuilderTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldManager;
import com.uwe_hennig.snn.contracts.graph.EdgeDirectionMode;
import com.uwe_hennig.snn.graph.generator.DefaultAfferentGraphGenerator;
import com.uwe_hennig.snn.graph.generator.DefaultAssociativeGraphGenerator;
import com.uwe_hennig.snn.graph.generator.DefaultEfferentGraphGenerator;

/**
 * NeuronFieldBuilderTest
 *
 * @author Uwe Hennig
 */
public class NeuronFieldBuilderTest {

    //@Test
    @DisplayName("Simple NeuronField pipeline Test")
    public void testSimplePipeline() {
        try {
            NeuronFieldBuilderImpl builder = new NeuronFieldBuilderImpl();

            builder.start()
                .withAfferent(new DefaultAfferentGraphGenerator(2, 0))
                .withAssociative(new DefaultAssociativeGraphGenerator(3, 1, 2))
                .withEfferent(new DefaultEfferentGraphGenerator(2, EdgeDirectionMode.FORWARD))
                .withFeedback(null)
                .build();

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        NeuronFieldManager.init(1000, 1000, 20);

        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterEach
    public void clearEach() {
        NeuronFieldManager.close();
    }

    @BeforeAll
    public static void beforeAll() {
        System.setProperty("snn.logging", "true");
    }

    @AfterAll
    public static void afterAll() {
        System.setProperty("snn.logging", "false");
    }
}
