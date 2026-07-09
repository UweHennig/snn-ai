/**
 * @(#)StimulusView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * StimulusView
 *
 * @author Uwe Hennig
 */
public class StimulusView {
    private final StimulusModel model;

    private final int           POOL_SIZE;
    private final int           MASK;
    private final long          TTL_NANO;
    private final AtomicInteger nextSearchStart = new AtomicInteger(0);

    public StimulusView(StimulusModel model, long ttl) {
        assert model != null : "Model must not bei null!";

        this.model = model;

        POOL_SIZE = model.getCapacity();
        MASK = POOL_SIZE - 1;
        TTL_NANO = ttl;
    }

    public StimulusModel getModel() {
        return model;
    }

    public int claimStimulus(int eventType, float value, int edgeRef) {
        long now = System.nanoTime(); // TODO check
        int start = nextSearchStart.getAndAdd(32) & MASK;

        for (int i = 0; i < 64; i++) {
            int index = (start + i) & MASK;

            if (model.getExpiry(index) < now) {
                if (model.tryWriteLock(index)) {
                    try {
                        if (model.getExpiry(index) < now) {

                            model.setEventType(index, eventType);
                            model.setEdgeRef(index, edgeRef);
                            model.setValue(index, value);
                            model.setExpiry(index, now + TTL_NANO);

                            return index;
                        }
                    } finally {
                        model.writeUnlock(index);
                    }
                }
            }
        }

        return -1;
    }

    public boolean updateStimulus(int index, int eventType, float value, int edgeRef) {
        long now = System.nanoTime(); // TODO check
        if (model.getExpiry(index) >= now) {
            if (model.tryWriteLock(index)) {
                try {
                    if (model.getExpiry(index) >= now) {
                        if (Math.abs(value) < 0.001) {
                            model.setExpiry(index, now - TTL_NANO);
                            return false;
                        }

                        model.setEventType(index, eventType);
                        model.setEdgeRef(index, edgeRef);
                        model.setValue(index, value);

                        return true;
                    }
                } finally {
                    model.writeUnlock(index);
                }
            }
        }

        return false;
    }

    public void invalidate(int index) {
        try {
            long now = System.nanoTime(); // TODO check
            model.writeLock(index);
            model.setExpiry(index, now - TTL_NANO);
        } finally {
            model.writeUnlock(index);
        }
    }

    public float getValue(int index) {
        return model.getValue(index);
    }

    public int getEventType(int index) {
        return model.getEventType(index);
    }

    public long getExpiry(int index) {
        return model.getExpiry(index);
    }

    public int getEdgeRef(int index) {
        return model.getEdgeRef(index);
    }
}
