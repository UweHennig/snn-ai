/**
 * @(#)AxonAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.MultiList;
import com.uwe_hennig.snn.anatomy.neuron.AxonModel;
import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorModel;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;

/**
 * AxonAllocator
 *
 * @author Uwe Hennig
 */
public class AxonAllocator {
    private static AxonAllocator INSTANCE;

    private final AxonModel model;
    private final ModulatorModel modulatorModel;
    private final MultiList multiList;

    private int nextOffset = 0;

    private AxonAllocator(int capacity, long maxMultiListBlocks, int minMuliListBytesPerBlock) {
        this.model = new AxonModel(capacity);
        this.modulatorModel = new ModulatorModel(capacity);
        this.multiList = new MultiList(maxMultiListBlocks, minMuliListBytesPerBlock);
    }

    public static AxonAllocator initInstance(int capacity, long maxMultiListBlocks, int minMuliListBytesPerBlock) {
        INSTANCE = new AxonAllocator(capacity, maxMultiListBlocks, minMuliListBytesPerBlock);
        return INSTANCE;
    }

    public static AxonAllocator instance() {
        return INSTANCE;
    }

    public AxonView newAxonView(int fieldId, int neuronId) {
        if (nextOffset >= model.getCapacity()) {
            throw new IllegalStateException("Out of Offheap memory");
        }
        int offset = nextOffset++;

        ModulatorView modulatorView = new ModulatorView(offset, modulatorModel);
        AxonView axonView = new AxonView(offset, model, modulatorView, multiList);
        axonView.setStructure(fieldId, neuronId, -1);

        return axonView;
    }

    public AxonView viewAt(int viewId) {
        if (viewId >= nextOffset) {
            return null;
        }
        ModulatorView modulatorView = new ModulatorView(viewId, modulatorModel);
        AxonView axonView = new AxonView(viewId, model, modulatorView, multiList);

        return axonView;
    }

    public void close() {
        model.close();
        modulatorModel.close();
        multiList.close();
    }
}
