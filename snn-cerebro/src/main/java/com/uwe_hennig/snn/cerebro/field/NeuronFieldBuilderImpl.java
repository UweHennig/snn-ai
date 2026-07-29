/**
 * @(#)NeuronFieldBuilderImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import java.util.HashSet;
import java.util.List;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldManager;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;
import com.uwe_hennig.snn.cerebro.contracts.FieldGraph;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;

/**
 * NeuronFieldBuilderImpl
 *
 * @author Uwe Hennig
 */
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder, GenerationContext {
    private HashSet<Long> bitSet = new HashSet<>();

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

            List<Graph> genGraphList = generator.generate(context, inputGraphList);
            return new EfferentStageImpl(genGraphList);
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
            List<Graph> genGraphList = generator.generate(context, inputGraphList);

            return new FeedbackStageImpl(genGraphList);
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
            List<Graph> genGraphList = generator.generate(context, inputGraphList);

            return new BuildStageImpl();
        }
    }

    private final class BuildStageImpl implements BuildStage {
        @Override
        public FieldGraph build() {
            // TODO
            return null;
        }
    }

    // --- GenerationContext methods ---

    @Override
    public int createNode(int type) {
        return NeuronFieldView.createNeuronField(type);
    }

    @Override
    public long connect(int src, int trg) {
        long edgeId = packEdge(src, trg);
        NeuronFieldView.addOutNeighbourIds(src, trg);
        NeuronFieldView.addInNeighbourIds(trg, trg);
        return edgeId;
    }

    @Override
    public boolean isUsed(long edgeId) {
        return bitSet.contains(edgeId);
    }

    @Override
    public void setUsed(long edgeId) {
        bitSet.add(edgeId);
    }

    // --- convenient methods ---

    private long packEdge(int srcId, int trgId) {
        long edgeId = ((long) srcId << 32) | (trgId & 0xFFFFFFFFL);
        return edgeId;
    }

    // private boolean contains(int id, int[] list) {
    // for (int i = 0; i < list.length; i++) {
    // if (list[i] == id) {
    // return true;
    // }
    // }
    // return false;
    // }
    // private int srcId(long edgeId) {
    // int srcId = (int) (edgeId >>> 32);
    // return srcId;
    // }
    // private int trgId(long edgeId) {
    // int trgId = (int) edgeId;
    // return trgId;
    // }
    // private static long pack(int src, int trg) {
    // long combined = ((long) trg << 32) | (src & 0xFFFFFFFFL);
    // return combined;
    // }
    // private static int unpackSrc(long value) {
    // return (int) (value & 0xFFFFFFFFL);
    // }
    // private static int unpackTrg(long value) {
    // return (int) (value >> 32);
    // }
    // private void addToFieldLists(NeuronFieldType type, NeuronField field) {
    // switch (type) {
    // case NeuronFieldType.AFFERENT:
    // afferent.add(field);
    // break;
    // case NeuronFieldType.EFFERENT:
    // efferent.add(field);
    // break;
    // case NeuronFieldType.FEEDBACK:
    // feedback.add(field);
    // break;
    // default:
    // associative.add(field);
    // break;
    // }
    // }
}
