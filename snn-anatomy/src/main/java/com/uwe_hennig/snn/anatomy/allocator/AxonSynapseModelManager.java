/**
 * @(#)AxonSynapseModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * AxonSynapseModelManager
 *
 * @author Uwe Hennig
 */
public class AxonSynapseModelManager {
    private static AxonSynapseModelManager INSTANCE;
    private MultiList multiList;

    private AxonSynapseModelManager(long maxBlocks, int minDataCapacityBytes) {
        multiList = new MultiList(maxBlocks, minDataCapacityBytes);
    }

    public static AxonSynapseModelManager instance() {
        return INSTANCE;
    }

    public static AxonSynapseModelManager init(long maxBlocks, int minDataCapacityBytes) {
        if (INSTANCE == null) {
            synchronized (AxonSynapseModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AxonSynapseModelManager(maxBlocks, minDataCapacityBytes);
                }
            }
        }
        return INSTANCE;
    }

    public int nextId() {
        return (int)multiList.allocate();
    }

    public MultiList getModel() {
        return multiList;
    }

    public void save(String folder) {
        /* TODO: Save model */
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.multiList.close();
            INSTANCE.multiList = null;
            INSTANCE = null;
        }
    }

    public void load(String folder) {
        /* TODO Load model */
    }
}
