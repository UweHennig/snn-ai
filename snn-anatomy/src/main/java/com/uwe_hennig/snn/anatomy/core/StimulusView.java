/**
 * @(#)StimulusView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * StimulusView
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class StimulusView {
    private final StimulusModel model;
    private final int index;

    private final int POOL_SIZE;
    private final int MASK;
    private final long TTL_NANO;
    private final AtomicInteger nextSearchStart = new AtomicInteger(0);

    public StimulusView(int index, StimulusModel model, long ttl) {
        assert model != null : "Model must not bei null!";
        assert index < model.capacity && index >=0 : " " + index + " >= " + model.capacity;

        this.index = index;
        this.model = model;

        POOL_SIZE = model.getCapacity();
        MASK = POOL_SIZE - 1;
        TTL_NANO = ttl;
    }

    public StimulusModel getModel() {
        return model;
    }

    public long getViewId() {
        return index;
    }

    public int claimStimulus(int src, int trg, int type, float value) {
        long now = System.nanoTime();
        int start = nextSearchStart.getAndAdd(32) & MASK;

        for (int i = 0; i < 64; i++) {
            int index = (start + i) & MASK;

            if (model.getExpiry(index) < now) {
                if (model.tryLock(index)) {
                    try {
                        if (model.getExpiry(index) < now) {
                            model.setSrcIndex(index, src);
                            model.setTrgIndex(index, trg);
                            model.setType(index, type);
                            model.setValue(index, value);

                            model.setExpiry(index, now + TTL_NANO);

                            return index;
                        }
                    } finally {
                        model.unlock(index);
                    }
                }
            }
        }

        return -1;
    }

    public boolean updateStimulus(int index, int src, int trg, int type, float value) {
        long now = System.nanoTime();
        if (model.getExpiry(index) >= now) {
            if (model.tryLock(index)) {
                try {
                    if (model.getExpiry(index) >= now) {
                        model.setSrcIndex(index, src);
                        model.setTrgIndex(index, trg);
                        model.setType(index, type);
                        model.setValue(index, value);

                        model.setExpiry(index, now + TTL_NANO);

                        return true;
                    }
                } finally {
                    model.unlock(index);
                }
            }
        }

        return false;
    }
}
