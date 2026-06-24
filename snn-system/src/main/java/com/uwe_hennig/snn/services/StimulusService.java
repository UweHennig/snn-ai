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
 * The StimulusService optimizes Java Arena access for frequently changing events, which in turn significantly reduces GC pressure.
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

    public static boolean update(int index, int eventType, int src, int trg, int trgRef, int trgType, float value) {
        return INSTANCE.view.updateStimulus(index, eventType, src, trg, trgRef, trgType, value);
    }

    public static int getSrc(int index) {
        return INSTANCE.view.getSrc(index);
    }

    public static int getTrg(int index) {
        return INSTANCE.view.getTrg(index);
    }

    public static int getTrgType(int index) {
        return INSTANCE.view.getTrgType(index);
    }

    public static long getExpiry(int index) {
        return INSTANCE.view.getExpiry(index);
    }

    public static boolean isTimeFeedback(int index) {
        return false;// TODO
    }
}


