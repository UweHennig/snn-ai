/**
 * @(#)NeuronFieldListManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * NeuronFieldListManager
 *
 * @author Uwe Hennig
 */
public class NeuronFieldListManager {
    private static NeuronFieldListManager INSTANCE;
    private MultiList multiList;

    private NeuronFieldListManager(long maxBlocks, int minDataCapacityBytes) {
        multiList = new MultiList(maxBlocks, minDataCapacityBytes);
    }

    public static NeuronFieldListManager init(long maxBlocks, int minDataCapacityBytes) {
        if (INSTANCE == null) {
            synchronized (SynapseListManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronFieldListManager(maxBlocks, minDataCapacityBytes);
                }
            }
        }
        return INSTANCE;
    }

    public static NeuronFieldListManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return (int)multiList.allocate();
    }

    public MultiList getModel() {
        return multiList;
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.multiList.close();
            INSTANCE.multiList = null;
            INSTANCE = null;
        }
    }

    public void save(String folder) {
        /* TODO: Save model */
    }

    public void load(String folder) {
        /* TODO Load model */
    }
}
