/**
 * @(#)StimulusView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * StimulusView
 * @author Uwe Hennig
 */
public class StimulusView {
    private final StimulusModel model;

    private final int POOL_SIZE;
    private final int MASK;
    private final long TTL_NANO;
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

    // TODO Multi Recipient
    public int claimStimulus(int src, int trg, int type, float value) {
        long now = System.nanoTime(); // TODO check
        int start = nextSearchStart.getAndAdd(32) & MASK;

        for (int i = 0; i < 64; i++) {
            int index = (start + i) & MASK;

            if (model.getExpiry(index) < now) {
                if (model.tryWriteLock(index)) {
                    try {
                        if (model.getExpiry(index) < now) {
                            model.setSrc(index, src);
                            model.setTrg(index, trg);
                            model.setTrgType(index, type);
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

    public boolean updateStimulus(int index, int eventType, int src, int trg, int trgRef, int trgType, float value) {
        long now = System.nanoTime(); // TODO check
        if (model.getExpiry(index) >= now) {
            if (model.tryWriteLock(index)) {
                try {
                    if (model.getExpiry(index) >= now) {
                        model.setSrc(index, src);
                        model.setTrg(index, trg);
                        model.setTrgRef(index, trgRef);
                        model.setTrgType(index, trgType);
                        model.setValue(index, value);
                        model.setEventType(index, eventType);

                        model.setExpiry(index, now + TTL_NANO);

                        return true;
                    }
                } finally {
                    model.writeUnlock(index);
                }
            }
        }

        return false;
    }

    public long getExpiry(int index) {
        return model.getExpiry(index);
    }

    public int getSrc(int index) {
        return model.getSrc(index);
    }

    public int getTrg(int index) {
        return model.getTrg(index);
    }

    public int getTrgType(int index) {
        return model.getTrgType(index);
    }

    public float getValue(int index) {
        return model.getValue(index);
    }

    public int getEventType(int index) {
        return model.getEventType(index);
    }

    public int [] getTrgList(int index) {
        // TODO
        return null;
    }

}
