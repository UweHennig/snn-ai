/**
 * @(#)StimulusService.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.services;

import com.uwe_hennig.snn.anatomy.core.StimulusModel;
import com.uwe_hennig.snn.anatomy.core.StimulusView;

/**
 * StimulusService
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class StimulusService {
    private static volatile StimulusService INSTANCE;
    private final StimulusView view;

    private StimulusService(int poolSize, long ttl) {
        StimulusModel model = new StimulusModel(poolSize);
        this.view = new StimulusView(model, ttl);
    }

    public static synchronized StimulusService of(int poolSize, long ttl) {
        if (INSTANCE != null) {
            throw new IllegalStateException("Already initialized");
        }
        INSTANCE = new StimulusService(poolSize, ttl);
        return INSTANCE;
    }

    public static int claim(int src, int trg, int type, float value) {
        return INSTANCE.view.claimStimulus(src, trg, type, value);
    }

    public static float getValue(int index) {
        return INSTANCE.view.getValue(index);
    }

    public static boolean update(int index, int src, int trg, int type, float value) {
        return INSTANCE.view.updateStimulus(index, src, trg, type, value);
    }

    public static int getSrc(int index) {
        return INSTANCE.view.getSrc(index);
    }

    public static int getTrg(int index) {
        return INSTANCE.view.getTrg(index);
    }

    public static int getType(int index) {
        return INSTANCE.view.getType(index);
    }

    public static long getExpiry(int index) {
        return INSTANCE.view.getExpiry(index);
    }
}


