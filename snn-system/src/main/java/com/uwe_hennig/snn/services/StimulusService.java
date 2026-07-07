/**
 * @(#)StimulusService.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.services;

import com.uwe_hennig.snn.anatomy.core.StimulusModel;
import com.uwe_hennig.snn.anatomy.core.StimulusView;
import com.uwe_hennig.snn.contracts.core.StimulusType;

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

    public static int claim(int eventType, float value, int edgeRef) {
        return INSTANCE.view.claimStimulus(eventType, value, edgeRef);
    }

    public static int update(int index, int eventType, float value, int edgeRef) {
        if (!INSTANCE.view.updateStimulus(index, eventType, value, edgeRef)) {
            return claim(eventType, value, edgeRef);
        }
        return index;
    }

    public static float getValue(int index) {
        return INSTANCE.view.getValue(index);
    }

    public static int getEventType(int index) {
        return INSTANCE.view.getEventType(index);
    }

    public static long getExpiry(int index) {
        return INSTANCE.view.getExpiry(index);
    }

    public static int getEdgeRef(int index) {
        return INSTANCE.view.getEdgeRef(index);
    }


    public static boolean isTimeFeedback(int index) {
        return StimulusType.TIME_FEEDBACK.code() == INSTANCE.view.getEventType(index);
    }

    public static boolean isValueFeedback(int index) {
        return StimulusType.VALUE_FEEDBACK.code() == INSTANCE.view.getEventType(index);
    }

    public static boolean isStimulus(int index) {
        return StimulusType.STIMULUS.code() == INSTANCE.view.getEventType(index);
    }
}


