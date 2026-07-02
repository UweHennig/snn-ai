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

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldAllocator;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;
import com.uwe_hennig.snn.cerebro.contracts.FieldGraph;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;
import com.uwe_hennig.snn.contracts.core.NeuronFieldType;

/**
 * NeuronFieldBuilderImpl
 *
 * @author Uwe Hennig
 */
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder {
    private final Deque<NeuronField> stack = new ArrayDeque<>();

    private List<NeuronField> afferent = new ArrayList<>();
    private List<NeuronField> associative = new ArrayList<>();
    private List<NeuronField> efferent = new ArrayList<>();
    private List<NeuronField> feedback = new ArrayList<>();

    public NeuronFieldBuilderImpl() {
        if (NeuronFieldAllocator.instance() == null) {
            throw new IllegalStateException("Initialize NeuronFieldAllocator before starting NeuronFieldBuilder");
        }
    }

    @Override
    public NeuronFieldBuilder withAfferent(int count, Consumer<NeuronFieldBuilder> each) {
        return internal(NeuronFieldType.AFFERENT, count, each);
    }

    @Override
    public NeuronFieldBuilder withAssociative(int count, Consumer<NeuronFieldBuilder> each) {
        return internal(NeuronFieldType.ASSOCIATIVE, count, each);
    }

    @Override
    public NeuronFieldBuilder withEfferent(int count, Consumer<NeuronFieldBuilder> each) {
        return internal(NeuronFieldType.EFFERENT, count, each);
    }

    @Override
    public NeuronFieldBuilder withFeedback(int count, Consumer<NeuronFieldBuilder> each) {
        return internal(NeuronFieldType.FEEDBACK, count, each);
    }

    @Override
    public FieldGraph build() {
        return new FieldGraph(afferent, associative, efferent, feedback);
    }

    private NeuronFieldBuilder internal(NeuronFieldType type, int count, Consumer<NeuronFieldBuilder> logic) {
        NeuronField parent = stack.peek();

        for (int i = 0; i < count; i++) {
            NeuronFieldView view = NeuronFieldAllocator.instance().newFieldView(type.code());
            NeuronField field = new NeuronField(view);
            addToFieldLists(type, field);

            if (parent != null) {
                parent.addOutNeighbour(view);
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
