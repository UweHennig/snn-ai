/**
 * @(#)NeuronFieldAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.MultiList;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldView;

/**
 * NeuronFieldAllocator
 *
 * @author Uwe Hennig
 */
public class NeuronFieldAllocator {
    private static NeuronFieldAllocator INSTANCE;

    private final NeuronFieldModel model;
    private final MultiList multiList;
    private int nextOffset = 0;

    public static NeuronFieldAllocator initInstance(int capacity, long maxMultiListBlocks, int minMuliListElementsPerBlock) {
        INSTANCE = new NeuronFieldAllocator(capacity, maxMultiListBlocks, minMuliListElementsPerBlock * 4);
        return INSTANCE;
    }

    public static NeuronFieldAllocator instance() {
        return INSTANCE;
    }

    private NeuronFieldAllocator(int capacity, long maxMultiListBlocks, int minMuliListBytesPerBlock) {
        this.model = new NeuronFieldModel(capacity);
        this.multiList = new MultiList(maxMultiListBlocks, minMuliListBytesPerBlock);
    }

    public NeuronFieldView newFieldView(int type) {
        if (nextOffset >= model.getCapacity()) {
            throw new IllegalStateException("Out of off heap neuron field memory");
        }
        int offset = nextOffset++;

        NeuronFieldView view = new NeuronFieldView(offset, model, multiList);
        view.resetRefs();
        view.setType(type);

        return view;
    }

    public NeuronFieldView viewAt(int viewId) {
        if (viewId <= nextOffset) {
            return null;
        }

        return new NeuronFieldView(viewId, model, multiList);
    }

    public void close() {
        model.close();
        multiList.close();
    }

    public void save(String folder) {
        /* TODO: Save model */
    }

    public void load(String folder) {
        /* TODO Load model */
    }
}
