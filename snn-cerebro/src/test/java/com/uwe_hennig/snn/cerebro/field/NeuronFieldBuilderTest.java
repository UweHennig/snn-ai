/**
 * @(#)NeuronFieldBuilderTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldListManager;
import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldModelManager;
import com.uwe_hennig.snn.cerebro.contracts.FieldGraph;
import com.uwe_hennig.snn.contracts.core.NeuronFieldType;

/**
 * NeuronFieldBuilderTest
 *
 * @author Uwe Hennig
 */
public class NeuronFieldBuilderTest {

    @Test
    @DisplayName("Simple NeuronField pipeline Test")
    public void testSimplePipeline() {
        NeuronFieldBuilderImpl b = new NeuronFieldBuilderImpl();

        FieldGraph g = b.withAfferent(1, aff -> aff.withAssociative(1, assoc -> assoc.withEfferent(1, null))).build();

        assertNotNull(g, "No FieldGraph created!");

        assertEquals(1, g.afferent().size(), "Invalid afferent size!");
        assertEquals(1, g.associative().size(), "Invalid associative size!");
        assertEquals(1, g.efferent().size(), "Invalid efferent size!");
        assertEquals(0, g.feedback().size(), "Invalid feedback size!");

        int[] counter = { 0 };
        NeuronField.visit(g.associative().get(0), _ -> {
            counter[0] += 1;
        });
        assertEquals(3, counter[0]);
    }

    @Test
    @DisplayName("Simple NeuronField branching Test")
    public void testBranching() {
        NeuronFieldBuilderImpl b = new NeuronFieldBuilderImpl();

        FieldGraph g = b.withAfferent(1, aff -> {
            aff.withAssociative(1, null);
            aff.withAssociative(1, null);
        }).build();

        assertNotNull(g, "No FieldGraph created!");

        assertEquals(1, g.afferent().size(), "Invalid afferent size!");
        assertEquals(2, g.associative().size(), "Invalid associative size!");
        assertEquals(0, g.efferent().size(), "Invalid efferent size!");
        assertEquals(0, g.feedback().size(), "Invalid feedback size!");

        NeuronField aff = g.afferent().get(0);
        NeuronField ass1 = g.associative().get(0);
        NeuronField ass2 = g.associative().get(1);

        assertTrue(aff.getOutNeighbourIds().contains(ass1.getViewId()));
        assertTrue(aff.getOutNeighbourIds().contains(ass2.getViewId()));

        int[] counter = { 0 };
        NeuronField.visit(ass1, _ -> {
            counter[0] += 1;
        });
        assertEquals(3, counter[0]);
    }

    @Test
    @DisplayName("Simple NeuronField feedback type Test")
    public void testFeedbackField() {
        NeuronFieldBuilderImpl b = new NeuronFieldBuilderImpl();

        FieldGraph g = b.withFeedback(1, fb -> fb.withAssociative(1, null)).build();
        assertNotNull(g, "No FieldGraph created!");

        assertEquals(0, g.afferent().size(), "Invalid afferent size!");
        assertEquals(1, g.associative().size(), "Invalid associative size!");
        assertEquals(0, g.efferent().size(), "Invalid efferent size!");
        assertEquals(1, g.feedback().size(), "Invalid feedback size!");

        NeuronField fb = g.feedback().get(0);
        NeuronField as = g.associative().get(0);

        assertEquals(NeuronFieldType.FEEDBACK, fb.type());
        assertTrue(fb.getOutNeighbourIds().contains(as.getViewId()));

        int[] counter = { 0 };
        NeuronField.visit(as, _ -> {
            counter[0] += 1;
        });
        assertEquals(2, counter[0]);
    }

    @Test
    @DisplayName("Simple NeuronField nested structure Test")
    public void testNestedStructure() {
        NeuronFieldBuilderImpl b = new NeuronFieldBuilderImpl();

        FieldGraph g = b.withAfferent(1, aff -> aff.withAssociative(1, assoc -> assoc.withAssociative(1, null))).build();
        assertNotNull(g, "No FieldGraph created!");

        assertEquals(1, g.afferent().size(), "Invalid afferent size!");
        assertEquals(2, g.associative().size(), "Invalid associative size!");
        assertEquals(0, g.efferent().size(), "Invalid efferent size!");
        assertEquals(0, g.feedback().size(), "Invalid feedback size!");

        NeuronField aff = g.afferent().get(0);
        assertNotNull(aff);
        NeuronField ass = g.associative().get(0);
        assertNotNull(ass);
        NeuronField deep = g.associative().get(1);
        assertNotNull(deep);

        assertTrue(ass.getInNeighbourIds().contains(aff.getViewId()));
        assertTrue(deep.getInNeighbourIds().contains(ass.getViewId()));

        int[] counter = { 0 };
        NeuronField.visit(deep, _ -> {
            counter[0] += 1;
        });
        assertEquals(3, counter[0]);
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        NeuronFieldModelManager.init(10);
        NeuronFieldListManager.init(100, 100);

        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterEach
    public void clearEach() {
        if (NeuronFieldModelManager.instance() != null) {
            NeuronFieldModelManager.instance();
            NeuronFieldModelManager.close();
        }
    }
}
