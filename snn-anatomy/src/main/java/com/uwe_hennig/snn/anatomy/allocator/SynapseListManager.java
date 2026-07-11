/**
 * @(#)SynapseListManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * SynapseListManager
 *
 * @author Uwe Hennig
 */
public class SynapseListManager {
    private static SynapseListManager INSTANCE;
    private MultiList multiList;

    private SynapseListManager(long maxBlocks, int minDataCapacityBytes) {
        multiList = new MultiList(maxBlocks, minDataCapacityBytes);
    }

    public static SynapseListManager instance() {
        return INSTANCE;
    }

    public static SynapseListManager init(long maxBlocks, int minDataCapacityBytes) {
        if (INSTANCE == null) {
            synchronized (SynapseListManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SynapseListManager(maxBlocks, minDataCapacityBytes);
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
