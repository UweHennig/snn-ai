/**
 * @(#)FeedbackModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.peripheral.FeedbackModel;

/**
 * FeedbackModelManager
 *
 * @author Uwe Hennig
 */
public class FeedbackModelManager {
    private static FeedbackModelManager INSTANCE;

    private FeedbackModel model;
    private int           nextOffset = 0;

    private FeedbackModelManager(int capacity) {
        this.model = new FeedbackModel(capacity);
    }

    public static FeedbackModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (ReceptorModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new FeedbackModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static FeedbackModelManager instance() {
        return INSTANCE;
    }

    public int nextId() {
        if (model.getCapacity() <= nextOffset) {
            throw new IllegalStateException("Out of off heap weight memory");
        }
        return nextOffset++;
    }

    public int capacity() {
        return nextOffset;
    }

    public FeedbackModel getModel() {
        return model;
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.model.close();
            INSTANCE.model = null;
            INSTANCE.nextOffset = 0;
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
