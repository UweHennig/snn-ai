/**
 * @(#)DendritListManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.MultiList;

/**
 * DendritListManager
 *
 * @author Uwe Hennig
 */
public class DendritListManager {
    private static DendritListManager INSTANCE;
    private MultiList multiList;

    private DendritListManager(long maxBlocks, int minDataCapacityBytes) {
        multiList = new MultiList(maxBlocks, minDataCapacityBytes);
    }

    public static DendritListManager init(long maxBlocks, int minDataCapacityBytes) {
        if (INSTANCE == null) {
            synchronized (SynapseListManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DendritListManager(maxBlocks, minDataCapacityBytes);
                }
            }
        }
        return INSTANCE;
    }

    public static DendritListManager  instance() {
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
