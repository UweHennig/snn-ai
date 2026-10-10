/**
 * @(#)NeuronBuilderTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import org.junit.jupiter.api.Disabled;

/**
 * NeuronBuilderTest
 * TODO
 * @author Uwe Hennig
 */
@Disabled
public class NeuronBuilderTest {
//
//    @Test
//    @DisplayName("Create Simple Neuron Test")
//    public void buildOneNeuonTest() {
//        NeuronBuilderImpl neuronBuilder = new NeuronBuilderImpl(0);
//        neuronBuilder.withDendrites(2);
//        neuronBuilder.withSynapses(2);
//        NeuronInfo graph = neuronBuilder.build();
//
//        assertNotNull(graph);
//        assertNotNull(graph.dendrites());
//        assertNotNull(graph.synapses());
//        assertNotNull(graph.soma());
//        assertNotNull(graph.axon());
//
//        assertEquals(2, graph.dendrites().size());
//        assertEquals(2, graph.synapses().size());
//
//        checkStructure(graph);
//    }
//
//    public void checkStructure(NeuronInfo graph) {
//        int fieldId = graph.fieldId();
//        assertTrue(fieldId >= 0);
//
//        int neuronId = graph.neuronId();
//        assertTrue(neuronId >= 0);
//
//        Soma soma = graph.soma();
//        assertEquals(neuronId, soma.getNeuronId());
//
//        Axon axon = graph.axon();
//        assertEquals(neuronId, axon.getNeuronId());
//
//        assertEquals(soma.getTargetId(), axon.getViewId());
//
//        for (Dendrit dendrit : graph.dendrites()) {
//            int viewId = dendrit.getViewId();
//            assertTrue(viewId >= 0);
//
//            assertEquals(neuronId, dendrit.getNeuronId());
//            assertEquals(soma.getViewId(), dendrit.getTargetId());
//        }
//
//        for (Synapse synapse : graph.synapses()) {
//            int viewId = synapse.getViewId();
//            assertTrue(viewId >= 0);
//            assertTrue(synapse.getTargetId() >= 0);
//            assertEquals(neuronId, synapse.getNeuronId());
//        }
//    }
//
//    @BeforeEach
//    public void beforeEach(TestInfo info) {
//        headline(info);
//        initMemory();
//    }
//
//    @AfterEach
//    public void afterEach() {
//        disposeMemory();
//    }
//
//    private void headline(TestInfo info) {
//        String title = "### " + info.getDisplayName() + " ###";
//        System.out.println("\n" + title);
//        System.out.println("-".repeat(title.length()));
//    }
//
//    private void initMemory() {
//        DendritModelManager.init(20);
//        WeightModelManager.init(20);
//
//        SomaModelMangager.init(10);
//        PotentialModelManager.init(10);
//        ThresholdModelManager.init(10);
//        PlasticityModelManager.init(10);
//
//        AxonModelManager.init(10);
//        SynapseModelManager.init(20);
//
//        ModulatorModelManager.init(30);
//
//        SynapseListManager.init(10, 60);
//
//        NeuronModelManager.init(10);
//
//        NeuronListManager.init(10, 40);
//
//        DendritListManager.init(10, 40);
//
//        // TODO StimulusModel
//    }
//
//    private void disposeMemory() {
//        DendritModelManager.close();
//        WeightModelManager.close();
//
//        SomaModelMangager.close();
//        PotentialModelManager.close();
//        ThresholdModelManager.close();
//        PlasticityModelManager.close();
//
//        AxonModelManager.close();
//        SynapseModelManager.close();
//
//        ModulatorModelManager.close();
//
//        SynapseListManager.close();
//
//        NeuronModelManager.close();
//
//        NeuronListManager.close();
//
//        DendritListManager.close();
//
//        // TODO StimulusModel
//
//    }
}
