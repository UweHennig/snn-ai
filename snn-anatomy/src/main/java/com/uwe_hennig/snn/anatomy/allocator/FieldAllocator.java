/**
 * @(#)FieldAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.MultiList;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;

/**
 * FieldAllocator
 *
 * @author Uwe Hennig
 */
public class FieldAllocator {
    private static FieldAllocator INSTANCE;

    private final NeuronFieldModel model;
    private final MultiList multiList;
    private int nextOffset = 0;

    public static FieldAllocator initInstance(int capacity, long maxMultiListBlocks, int minMuliListBytesPerBlock) {
        INSTANCE = new FieldAllocator(capacity, maxMultiListBlocks, minMuliListBytesPerBlock);
        return INSTANCE;
    }

    public static FieldAllocator instance() {
        return INSTANCE;
    }

    private FieldAllocator(int capacity, long maxMultiListBlocks, int minMuliListBytesPerBlock) {
        this.model = new NeuronFieldModel(capacity);
        this.multiList = new MultiList(maxMultiListBlocks, minMuliListBytesPerBlock);
    }

    public NeuronFieldView newFieldView(int type, int level) {
        if (nextOffset >= model.getCapacity()) {
            throw new IllegalStateException("Out of Offheap memory");
        }
        int offset = nextOffset++;

        NeuronFieldView view = new NeuronFieldView(offset, model, multiList);
        view.setLevel(level);
        view.setType(type);

        return view;
    }

    public NeuronFieldView viewAt(int viewId) {
        if (viewId >= nextOffset) {
            return null;
        }

        return new NeuronFieldView(viewId, model, multiList);
    }
}
