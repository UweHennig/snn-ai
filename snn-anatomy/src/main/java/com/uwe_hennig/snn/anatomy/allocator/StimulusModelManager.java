/**
 * @(#)StimulusModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.core.StimulusModel;

/**
 * StimulusModelManager
 *
 * @author Uwe Hennig
 */
public class StimulusModelManager {
    private static StimulusModelManager INSTANCE;

    private StimulusModel model;

    private StimulusModelManager(int capacity) {
        this.model = new StimulusModel(capacity);
    }

    public static StimulusModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (StimulusModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StimulusModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static StimulusModelManager instance() {
        return INSTANCE;
    }

    public StimulusModel getModel() {
        return model;
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.model.close();
            INSTANCE.model = null;
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
