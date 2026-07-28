/**
 * @(#)NeuronFieldManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.MultiList;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;

/**
 * NeuronFieldManager
 *
 * @author Uwe Hennig
 */
public class NeuronFieldManager {
    private static NeuronFieldManager INSTANCE;

    private NeuronFieldModel model;
    private MultiList        multiList;

    private int nextOffset = 0;

    private NeuronFieldManager(int modelCapacity, long maxBlocks, int minDataCapacityBytes) {
        model = new NeuronFieldModel(modelCapacity);
        maxBlocks = Math.max(maxBlocks, modelCapacity);
        multiList = new MultiList(maxBlocks, minDataCapacityBytes);
    }

    public static NeuronFieldManager init(int modelCapacity, long maxBlocks, int minDataCapacityBytes) {
        if (INSTANCE == null) {
            synchronized (NeuronFieldManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronFieldManager(modelCapacity, maxBlocks, minDataCapacityBytes);
                }
            }
        }
        return INSTANCE;
    }

    public static NeuronFieldManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap neuron field memory");
        }
        return nextOffset++;
    }

    public NeuronFieldModel getModel() {
        return model;
    }

    public MultiList getListModel() {
        return multiList;
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.model.close();
            INSTANCE.model = null;

            INSTANCE.multiList.close();
            INSTANCE.multiList = null;

            INSTANCE = null;
        }
    }

}
