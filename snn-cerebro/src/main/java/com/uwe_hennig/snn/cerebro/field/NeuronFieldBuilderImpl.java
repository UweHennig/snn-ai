/**
 * @(#)NeuronFieldBuilderImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldManager;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView.NeuronFieldData;
import com.uwe_hennig.snn.cerebro.contracts.FieldGraph;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;
import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;

/**
 * NeuronFieldBuilderImpl
 *
 * @author Uwe Hennig
 */
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder, GenerationContext {
    private HashSet<Long>     bitSet      = new HashSet<>();
    private List<NeuronField> afferent    = new ArrayList<>();
    private List<NeuronField> associative = new ArrayList<>();
    private List<NeuronField> efferent    = new ArrayList<>();
    private List<NeuronField> feedback    = new ArrayList<>();

    public NeuronFieldBuilderImpl() {
        if (NeuronFieldManager.instance() == null) {
            throw new IllegalStateException("Initialize NeuronFieldAllocator before starting NeuronFieldBuilder");
        }
    }

    public AfferentStage start() {
        return new AfferentStageImpl();
    }

    // --- private Stage-Implementations ---

    private final class AfferentStageImpl implements AfferentStage {
        @Override
        public AssociativeStage withAfferent(GraphGenerator generator) {
            GenerationContext context = NeuronFieldBuilderImpl.this;

            List<Graph> genGraphList = generator.generate(context, null);
            return new AssociativeStageImpl(genGraphList);
        }
    }

    private final class AssociativeStageImpl implements AssociativeStage {
        private List<Graph> inputGraphList;

        public AssociativeStageImpl(List<Graph> inputGraphList) {
            this.inputGraphList = inputGraphList;
        }

        @Override
        public EfferentStage withAssociative(GraphGenerator generator) {
            GenerationContext context = NeuronFieldBuilderImpl.this;

            List<Graph> resultGraph = new ArrayList<>();

            for (Graph graph : inputGraphList) {
                List<Graph> genGraphList = generator.generate(context, graph);
                resultGraph.addAll(genGraphList);
            }

            return new EfferentStageImpl(resultGraph);
        }
    }

    private final class EfferentStageImpl implements EfferentStage {
        private List<Graph> inputGraphList;

        public EfferentStageImpl(List<Graph> inputGraphList) {
            this.inputGraphList = inputGraphList;
        }

        @Override
        public FeedbackStage withEfferent(GraphGenerator generator) {
            GenerationContext context = NeuronFieldBuilderImpl.this;
            List<Graph> resultGraph = new ArrayList<>();

            for (Graph graph : inputGraphList) {
                List<Graph> genGraphList = generator.generate(context, graph);
                resultGraph.addAll(genGraphList);
            }

            return new FeedbackStageImpl(resultGraph);
        }
    }

    private final class FeedbackStageImpl implements FeedbackStage {
        private List<Graph> inputGraphList;

        public FeedbackStageImpl(List<Graph> inputGraphList) {
            this.inputGraphList = inputGraphList;
        }

        @Override
        public BuildStage withFeedback(GraphGenerator generator) {
            GenerationContext context = NeuronFieldBuilderImpl.this;
            List<Graph> resultGraph = new ArrayList<>();

            for (Graph graph : inputGraphList) {
                List<Graph> genGraphList = generator.generate(context, graph);
                resultGraph.addAll(genGraphList);
            }

            return new BuildStageImpl();
        }
    }

    private final class BuildStageImpl implements BuildStage {
        @Override
        public FieldGraph build() {
            return new  FieldGraph(afferent, associative, efferent, feedback);
        }
    }

    // --- GenerationContext methods ---

    @Override
    public int createNode(NeuronFieldType type) {
        int neuronFieldId = NeuronFieldView.createNeuronField(type.code());
        NeuronFieldData data = NeuronFieldView.getData(neuronFieldId);
        NeuronField field = new NeuronField(neuronFieldId, data.neuronRef(), data.outRef(), data.inRef());
        switch (type) {
            case AFFERENT: afferent.add(field); break;
            case ASSOCIATIVE: associative.add(field); break;
            case EFFERENT: efferent.add(field); break;
            case FEEDBACK: feedback.add(field);break;
            default:;
        }
        return neuronFieldId;
    }

    @Override
    public long createEdge(int src, int trg) {
        long edgeId = packEdge(src, trg);
        NeuronFieldView.addOutNeighbourIds(src, trg);
        NeuronFieldView.addInNeighbourIds(trg, trg);
        return edgeId;
    }

    @Override
    public boolean isUsedEdge(long edgeId) {
        return bitSet.contains(edgeId);
    }

    @Override
    public void setUsedEdge(long edgeId) {
        bitSet.add(edgeId);
    }

    // --- convenient methods ---

    private long packEdge(int srcId, int trgId) {
        long edgeId = ((long) srcId << 32) | (trgId & 0xFFFFFFFFL);
        return edgeId;
    }
}
