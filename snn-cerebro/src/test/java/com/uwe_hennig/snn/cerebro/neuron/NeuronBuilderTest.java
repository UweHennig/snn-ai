/**
 * @(#)NeuronBuilderTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.anatomy.allocator.AxonModelManager;
import com.uwe_hennig.snn.anatomy.allocator.SynapseListManager;
import com.uwe_hennig.snn.anatomy.allocator.DendritListManager;
import com.uwe_hennig.snn.anatomy.allocator.DendritModelManager;
import com.uwe_hennig.snn.anatomy.allocator.EdgeModelManager;
import com.uwe_hennig.snn.anatomy.allocator.ModulatorModelManager;
import com.uwe_hennig.snn.anatomy.allocator.NeuronListManager;
import com.uwe_hennig.snn.anatomy.allocator.NeuronModelManager;
import com.uwe_hennig.snn.anatomy.allocator.PlasticityModelManager;
import com.uwe_hennig.snn.anatomy.allocator.PotentialModelManager;
import com.uwe_hennig.snn.anatomy.allocator.SomaModelMangager;
import com.uwe_hennig.snn.anatomy.allocator.SynapseModelManager;
import com.uwe_hennig.snn.anatomy.allocator.ThresholdModelManager;
import com.uwe_hennig.snn.anatomy.allocator.WeightModelManager;
import com.uwe_hennig.snn.cerebro.contracts.NeuronGraph;

/**
 * NeuronBuilderTest
 *
 * @author Uwe Hennig
 */
public class NeuronBuilderTest {

    @Test
    @DisplayName("Create Simple Neuron Test")
    public void buildOneNeuonTest() {
        NeuronBuilderImpl neuronBuilder = new NeuronBuilderImpl(0);
        neuronBuilder.withDendrites(2);
        neuronBuilder.withSynapses(2);
        NeuronGraph graph = neuronBuilder.build();

        assertNotNull(graph);
        assertNotNull(graph.dendrites());
        assertNotNull(graph.synapses());
        assertNotNull(graph.soma());
        assertNotNull(graph.axon());

        assertEquals(2, graph.dendrites().size());
        assertEquals(2, graph.synapses().size());

        checkInternalStructure();
    }

    public void checkInternalStructure() {
        // TODO
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        headline(info);
        initMemory();
    }

    @AfterEach
    public void afterEach() {
        disposeMemory();
    }

    private void headline(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    private void initMemory() {
        DendritModelManager.init(20);
        WeightModelManager.init(20);

        SomaModelMangager.init(10);
        PotentialModelManager.init(10);
        ThresholdModelManager.init(10);
        PlasticityModelManager.init(10);

        AxonModelManager.init(10);
        SynapseModelManager.init(20);

        ModulatorModelManager.init(30);

        SynapseListManager.init(10, 60);

        EdgeModelManager.init(60);

        NeuronModelManager.init(10);

        NeuronListManager.init(10, 40);

        DendritListManager.init(10, 40);

        // TODO StimulusModel
    }

    private void disposeMemory() {
        DendritModelManager.close();
        WeightModelManager.close();

        SomaModelMangager.close();
        PotentialModelManager.close();
        ThresholdModelManager.close();
        PlasticityModelManager.close();

        AxonModelManager.close();
        SynapseModelManager.close();

        ModulatorModelManager.close();

        SynapseListManager.close();

        EdgeModelManager.close();

        // TODO StimulusModel

    }
}
