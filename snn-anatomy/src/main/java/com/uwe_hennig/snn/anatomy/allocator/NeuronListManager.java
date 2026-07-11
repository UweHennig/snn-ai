/**
 * @(#)NeuronListManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * NeuronListManager
 *
 * @author Uwe Hennig
 */
public class NeuronListManager {
    private static NeuronListManager INSTANCE;
    private MultiList                multiList;

    private NeuronListManager(long maxBlocks, int minDataCapacityBytes) {
        multiList = new MultiList(maxBlocks, minDataCapacityBytes);
    }

    public static NeuronListManager init(long maxBlocks, int minDataCapacityBytes) {
        if (INSTANCE == null) {
            synchronized (SynapseListManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronListManager(maxBlocks, minDataCapacityBytes);
                }
            }
        }
        return INSTANCE;
    }

    public static NeuronListManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return (int) multiList.allocate();
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
