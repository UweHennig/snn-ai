/// @(#)NeuronFieldModelTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        NeuronFieldModel model = NeuronFieldModelManager.init(1_000_000).getModel();
        assertEquals(32, model.getEndOffset());

        int fieldA = model.claimField();
        int fieldB = model.claimField();

        model.setFieldType(fieldA, 42);
        model.setFieldType(fieldB, 31);

        assertEquals(42, model.getFieldType(fieldA), "invalid field type 42");
        assertEquals(31, model.getFieldType(fieldB), "invalid field type 31");

        int magic = ('N' << 24) | ('F' << 16) | ('M' << 8) | '1';
        assertEquals(magic, model.getMagic(), "invalid magic");

        for (int i = 0; i < 4; i++) {
            model.setFieldId(fieldA, i, i + 10);
            model.setFieldId(fieldB, i, i + 20);
        }

        for (int i = 0; i < 4; i++) {
            int idA = model.getFieldId(fieldA, i);
            assertEquals(i + 10, idA, "invalid field idA @" + i);

            int idB = model.getFieldId(fieldB, i);
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
