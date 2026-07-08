/**
 * @(#)SomaModelMangager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.SomaModel;

/**
 * SomaModelMangager
 *
 * @author Uwe Hennig
 */
public class SomaModelMangager {
    private static SomaModelMangager INSTANCE;
    private SomaModel                model;
    private int                      nextOffset = 0;

    private SomaModelMangager(int capacity) {
        this.model = new SomaModel(capacity);
    }

    public static SomaModelMangager init(int capacity) {
        INSTANCE = new SomaModelMangager(capacity);
        return INSTANCE;
    }

    public static SomaModelMangager instance() {
        return INSTANCE;
    }

    public int nextId() {
        return nextOffset++;
    }

    public SomaModel getModel() {
        return model;
    }

    public void close() {
        nextOffset = 0;
        model.close();
        model = null;
        INSTANCE = null;
    }
}
