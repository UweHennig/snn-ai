/// @(#)NeuronFieldModelTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
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

    /// Test structure
    /// ---- Meta
    ///    00: Meta
    ///    ....
    ///    28: endOffset
    /// ---- header A
    ///    32: Field A lock
    ///    36: Field A numNeurons
    ///    40: Field A maxNeurons
    ///    44: Field A numInNeighbours
    ///    48: Field A maxInNeighbours
    ///    52: Field A numOutNeighbours
    ///    56: Field A maxOutNeighbours
    ///    60: Field A numData
    ///    64: Field A max data
    /// ---- variable A
    ///    68: Field A neuron 1
    ///    72: Field A neuron 2
    ///    76: Field A In-field 1
    ///    80: Field A In-field 2
    ///    84: Field A Out-field 1
    ///    88: Field A Out-field 2
    ///    92: Field A data 1
    /// ---- header B
    ///    96: Field B lock
    ///   100: Field B numNeurons
    ///   104: Field B maxNeurons
    ///   108: Field B numInNeighbours
    ///   112: Field B maxInNeighbours
    ///   116: Field B numOutNeighbours
    ///   120: Field B maxOutNeighbours
    ///   124: Field B numData
    ///   128: Field B max data
    /// ---- variable B
    ///   132: Field B neuron 1
    ///   136: Field B neuron 2
    ///   140: Field B neuron 3
    ///   144: Field B In-field 1
    ///   148: Field B Out-field 1
    ///   152: Field B Out-field 2
    ///   156: Field B date 1
    @Test
    @DisplayName("Simple Data NeuronFieldModelTest")
    public void dataTest() {
        NeuronFieldModel model = NeuronFieldModelManager.init(1_000_000).getModel();
        assertEquals(32, model.getEndOffset());

        int fieldA = model.claimField(2, 2, 2, 1);
        int fieldB = model.claimField(3, 1, 2, 1);

        model.setFieldType(fieldA, 42);
        model.setFieldType(fieldB, 31);

        assertEquals(2, model.getEntryCount(), "invalid entry count");
        assertEquals(0, model.getNumNeurons(fieldA), "invalid neurons count A");
        assertEquals(0, model.getNumNeurons(fieldB), "invalid neurons count B");

        assertEquals(2, model.getMaxNeurons(fieldA), "invalid max neurons A");
        assertEquals(3, model.getMaxNeurons(fieldB), "invalid max neurons B");

        assertEquals(2, model.getMaxInNeighbours(fieldA), "invalid max in neibhbours A");
        assertEquals(1, model.getMaxInNeighbours(fieldB), "invalid max in neibhbours B");

        assertEquals(2, model.getMaxOutNeighbours(fieldA), "invalid max out neighbours A");
        assertEquals(2, model.getMaxOutNeighbours(fieldB), "invalid max out neighbours B");

        assertEquals(1, model.getMaxData(fieldA), "invalid max data A");
        assertEquals(1, model.getMaxData(fieldB), "invalid max data B");

        model.addNeuron(fieldA, 50);
        model.addNeuron(fieldA, 60);

        model.addNeuron(fieldB, 70);
        model.addNeuron(fieldB, 80);
        model.addNeuron(fieldB, 90);

        model.addInField(fieldA, 10);
        model.addInField(fieldA, 20);
        model.addInField(fieldB, 30);

        model.addOutField(fieldA, 100);
        model.addOutField(fieldA, 200);

        model.addOutField(fieldB, 300);
        model.addOutField(fieldB, 400);

        int [] expecedNeuronsA = {50, 60};
        int [] neuronsA = model.getNeuronIds(fieldA);
        assertEquals(2, model.getNumNeurons(fieldA), "invalid num neurons A");
        assertArrayEquals(expecedNeuronsA, neuronsA, "invalid neuron array A");

        int [] expecedNeuronsB = {70, 80, 90};
        int [] neuronsB = model.getNeuronIds(fieldB);
        assertEquals(3, model.getNumNeurons(fieldB), "invalid num neurons B");
        assertArrayEquals(expecedNeuronsB, neuronsB, "invalid neuron array B");

        int [] expectedInFieldsA = {10, 20};
        int [] inFieldsA = model.getInFields(fieldA);
        assertEquals(2, model.getNumInNeighbours(fieldA), "invalid num in fields A");
        assertArrayEquals(expectedInFieldsA, inFieldsA, "invalid in field array A");

        int [] expectedInFieldsB = {30};
        int [] inFieldsB = model.getInFields(fieldB);
        assertEquals(1, model.getNumInNeighbours(fieldB), "invalid num in fields A");
        assertArrayEquals(expectedInFieldsB, inFieldsB, "invalid in field array B");

        int [] expectedOutFieldsA = {100, 200};
        int [] outFieldsA = model.getOutFields(fieldA);
        assertEquals(2, model.getNumOutNeighbours(fieldA), "invalid num out fields A");
        assertArrayEquals(expectedOutFieldsA, outFieldsA, "invalid out field array A");

        int [] expectedOutFieldsB = {300, 400};
        int [] outFieldsB = model.getOutFields(fieldB);
        assertEquals(2, model.getNumOutNeighbours(fieldB), "invalid num out fields B");
        assertArrayEquals(expectedOutFieldsB, outFieldsB, "invalid out field array B");

        assertEquals(1, model.getNumData(fieldA), "invalid num data A");
        assertEquals(1, model.getNumData(fieldB), "invalid num data B");
        assertEquals(42, model.getFieldType(fieldA), "invalid field type A");
        assertEquals(31, model.getFieldType(fieldB), "invalid field type B");

        int magic = ('N' << 24) | ('F' << 16) | ('M' << 8) | '1';
        assertEquals(magic, model.getMagic(), "invalid magic");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
