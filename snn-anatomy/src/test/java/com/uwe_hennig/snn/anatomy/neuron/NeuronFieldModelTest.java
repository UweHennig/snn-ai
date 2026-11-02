/// @(#)NeuronFieldModelTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldModelManager;

/// NeuronFieldModelTest
///
/// @author Uwe Hennig
///
public class NeuronFieldModelTest {

    @Test
    @DisplayName("Simple Data NeuronFieldModelTest")
    public void dataTest() {
        final int NUM_FIELDS = 10;
        final int MAX_NEIGHBOURS = 3;

        NeuronFieldModel model = NeuronFieldModelManager.init(NUM_FIELDS, MAX_NEIGHBOURS).getModel();
        assertEquals(32, model.getEndOffset());

        int fieldA = model.claimField();
        int fieldB = model.claimField();

        assertEquals(32, fieldA, "invalid offset fieldA");
        assertEquals(60, fieldB, "invalid offset fieldB");

        model.setFieldType(fieldA, 42);
        model.setFieldType(fieldB, 31);

        assertEquals(42, model.getFieldType(fieldA), "invalid field type 42");
        assertEquals(31, model.getFieldType(fieldB), "invalid field type 31");

        int magic = ('N' << 24) | ('F' << 16) | ('M' << 8) | '1';
        assertEquals(magic, model.getMagic(), "invalid magic");

        for (int i = 0; i < MAX_NEIGHBOURS; i++) {
            assertEquals(i, model.getNumNeighbours(fieldA));
            assertEquals(i, model.getNumNeighbours(fieldB));

            model.addNeighbourId(fieldA, i + 10);
            model.addNeighbourId(fieldB, i + 20);

            assertEquals(i + 1, model.getNumNeighbours(fieldA));
            assertEquals(i + 1, model.getNumNeighbours(fieldB));
        }

        for (int i = 0; i < MAX_NEIGHBOURS; i++) {
            int idA = model.getNeighbourId(fieldA, i);
            assertEquals(i + 10, idA, "invalid field idA @" + i);

            int idB = model.getNeighbourId(fieldB, i);
            assertEquals(i + 20, idB, "invalid field idB @" + i);
        }

        int [] allNeighboursA = model.getNeighbours(fieldA);
        int [] allNeighboursB = model.getNeighbours(fieldB);

        assertNotNull(allNeighboursA, "allNeighboursA is null");
        assertNotNull(allNeighboursB, "allNeighboursA is null");

        assertEquals(MAX_NEIGHBOURS, allNeighboursA.length, "invalid length allNeighboursA");
        assertEquals(MAX_NEIGHBOURS, allNeighboursB.length, "invalid length allNeighboursB");

        for (int i = 0; i < MAX_NEIGHBOURS; i++) {
            int idA = allNeighboursA[i];
            assertEquals(i + 10, idA, "invalid field idA @" + i);
            int idB = allNeighboursB[i];
            assertEquals(i + 20, idB, "invalid field idB @" + i);
        }
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
