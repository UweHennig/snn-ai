/**
 * @(#)NeuronFieldBuilderImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.field;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldManager;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView.NeuronFieldData;
import com.uwe_hennig.snn.cerebro.contracts.FieldGraph;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder.AfferentStage;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder.AssociativeStage;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder.BuildStage;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder.EfferentStage;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder.FeedbackStage;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder.Start;
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
    private final Deque<NeuronField> stack = new ArrayDeque<>();

    private List<NeuronField> afferent    = new ArrayList<>();
    private List<NeuronField> associative = new ArrayList<>();
    private List<NeuronField> efferent    = new ArrayList<>();
    private List<NeuronField> feedback    = new ArrayList<>();

    private NeuronFieldManager fieldManager;

    public NeuronFieldBuilderImpl() {
        if (NeuronFieldManager.instance() == null) {
            throw new IllegalStateException("Initialize NeuronFieldAllocator before starting NeuronFieldBuilder");
        }
        fieldManager = NeuronFieldManager.instance();
    }

    public Start start() {
        return new StartStage();
    }

    // --- private Stage-Implementations ---

    private final class StartStage implements Start {
        @Override
        public AfferentStage start() {
            return new AfferentStageImpl();
        }
    }

    private final class AfferentStageImpl implements AfferentStage {
        @Override
        public AssociativeStage withAfferent(GraphGenerator generator) {
            // TODO
            List<Graph> genGraphList = generator.generate(null, null);
            return new AssociativeStageImpl();
        }
    }

    private final class AssociativeStageImpl implements AssociativeStage {
        @Override
        public EfferentStage withAssociative(GraphGenerator generator) {
            // TODO
            return new EfferentStageImpl();
        }
    }

    private final class EfferentStageImpl implements EfferentStage {
        @Override
        public FeedbackStage withEfferent(GraphGenerator generator) {
            // TODO
            return new FeedbackStageImpl();
        }
    }

    private final class FeedbackStageImpl implements FeedbackStage {
        @Override
        public BuildStage withFeedback(GraphGenerator generator) {
            // TODO
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
        // TODO
        return -1;
    }

    @Override
    public long connect(int src, int trg) {
        long edgeId = packEdge(src, trg);

        // TODO

        return edgeId;
    }

    @Override
    public long createEgeId(int srcNodeId) {
        // TODO Auto-generated method stub class GenerationContext
        return 0;
    }

    @Override
    public boolean isUsed(long edgeId) {
        // TODO Auto-generated method stub class GenerationContext
        return false;
    }

    @Override
    public void setUsed(long edgeId) {
        // TODO Auto-generated method stub class GenerationContext

    }

    // --- internal helper methods ---

    private boolean contains(int id, int[] list) {
        for (int i = 0; i < list.length; i++) {
            if (list[i] == id) {
                return true;
            }
        }
        return false;
    }

    private long packEdge(int srcId, int trgId) {
        long edgeId = ((long) srcId << 32) | (trgId & 0xFFFFFFFFL);
        return edgeId;
    }

    private int srcId(long edgeId) {
        int srcId = (int) (edgeId >>> 32);
        return srcId;
    }

    private int trgId(long edgeId) {
        int trgId = (int) edgeId;
        return trgId;
    }

    private NeuronFieldBuilder internal(NeuronFieldType type, int count, Consumer<NeuronFieldBuilder> logic) {
        NeuronField parent = stack.peek();

//        for (int i = 0; i < count; i++) {
//            int newFieldId = NeuronFieldModelManager.instance().nextId();
//
//            // TODO das hier ist falsch!
//            int outRef = NeuronFieldListManager.instance().nextListRef();
//            int inRef = NeuronFieldListManager.instance().nextListRef();
//            int neuronRef = NeuronFieldListManager.instance().nextListRef();
//
//            NeuronFieldView.setRef(newFieldId, neuronRef, outRef, inRef);
//            NeuronFieldView.setType(newFieldId, type.code());
//
//            NeuronField field = new NeuronField(newFieldId, neuronRef, outRef, inRef);
//            addToFieldLists(type, field);
//
//            if (parent != null) {
//                parent.addOutNeighbour(field);
//                field.addInNeighbour(parent);
//            }
//
//            if (logic != null) {
//                stack.push(field);
//                logic.accept(this);
//                stack.pop();
//            }
//        }

        return this;
    }

    private void addToFieldLists(NeuronFieldType type, NeuronField field) {
        switch (type) {
            case NeuronFieldType.AFFERENT:
                afferent.add(field);
                break;
            case NeuronFieldType.EFFERENT:
                efferent.add(field);
                break;
            case NeuronFieldType.FEEDBACK:
                feedback.add(field);
                break;
            default:
                associative.add(field);
                break;
        }
    }
}
