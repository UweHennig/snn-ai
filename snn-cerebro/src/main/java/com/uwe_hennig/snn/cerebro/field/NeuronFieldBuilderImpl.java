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

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldListManager;
import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldModelManager;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;
import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;

/**
 * NeuronFieldBuilderImpl
 *
 * @author Uwe Hennig
 */
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder, GenerationContext {
    private final Deque<NeuronField> stack = new ArrayDeque<>();

    private List<NeuronField> afferent = new ArrayList<>();
    private List<NeuronField> associative = new ArrayList<>();
    private List<NeuronField> efferent = new ArrayList<>();
    private List<NeuronField> feedback = new ArrayList<>();

    public NeuronFieldBuilderImpl() {
        if (NeuronFieldModelManager.instance() == null) {
            throw new IllegalStateException("Initialize NeuronFieldAllocator before starting NeuronFieldBuilder");
        }
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
            return new AssociativeStageImpl();
        }
    }

    private final class AssociativeStageImpl implements AssociativeStage {
        @Override
        public EfferentStage withAssociative(GraphGenerator generator) {
            return new EfferentStageImpl();
        }
    }

    private final class EfferentStageImpl implements EfferentStage {
        @Override
        public FeedbackStage withEfferent(GraphGenerator generator) {
            return new FeedbackStageImpl();
        }
    }

    private final class FeedbackStageImpl implements FeedbackStage {
        @Override
        public BuildStage withFeedback(GraphGenerator generator) {
            return new BuildStageImpl();
        }
    }

    // --- GenerationContext methods ---

    @Override
    public int nextNodeId() {
        // TODO Auto-generated method stub class GenerationContext
        return 0;
    }

    private final class BuildStageImpl implements BuildStage {
        @Override
        public NeuronField build() {
            // TODO
            return null;
        }
    }

    @Override
    public long connect(int src, int trg) {
        // TODO Auto-generated method stub class GenerationContext
        return 0;
    }


    @Override
    public boolean isUsed(int src, int trg) {
        // TODO Auto-generated method stub class GenerationContext
        return false;
    }


    @Override
    public void setUsed(int src, int trg) {
        // TODO Auto-generated method stub class GenerationContext
    }

    // --- internal helper methods ---


    private NeuronFieldBuilder internal(NeuronFieldType type, int count, Consumer<NeuronFieldBuilder> logic) {
        NeuronField parent = stack.peek();

        for (int i = 0; i < count; i++) {
            int newFieldId = NeuronFieldModelManager.instance().nextId();

            // TODO das hier ist falsch!
            int outRef = NeuronFieldListManager.instance().nextId();
            int inRef = NeuronFieldListManager.instance().nextId();
            int neuronRef = NeuronFieldListManager.instance().nextId();

            NeuronFieldView.setRef(newFieldId, neuronRef, outRef, inRef);
            NeuronFieldView.setType(newFieldId, type.code());

            NeuronField field = new NeuronField(newFieldId, neuronRef, outRef, inRef);
            addToFieldLists(type, field);

            if (parent != null) {
                parent.addOutNeighbour(field);
                field.addInNeighbour(parent);
            }

            if (logic != null) {
                stack.push(field);
                logic.accept(this);
                stack.pop();
            }
        }

        return this;
    }

    private void addToFieldLists(NeuronFieldType type, NeuronField field) {
        switch(type) {
            case NeuronFieldType.AFFERENT: afferent.add(field); break;
            case NeuronFieldType.EFFERENT: efferent.add(field); break;
            case NeuronFieldType.FEEDBACK: feedback.add(field); break;
            default : associative.add(field);break;
        }
    }
}
