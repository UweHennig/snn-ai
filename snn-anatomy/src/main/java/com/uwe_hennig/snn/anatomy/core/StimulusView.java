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
@Deprecated
public class StimulusView {
    private final StimulusModel model;

    private final int           POOL_SIZE;
    private final int           MASK;
    private final long          TTL_NANO;
    private final AtomicInteger nextSearchStart = new AtomicInteger(0);

    @Deprecated
    public StimulusView(StimulusModel model, long ttl) {
        assert model != null : "Model must not bei null!";

        this.model = model;

        POOL_SIZE = model.getCapacity();
        MASK = POOL_SIZE - 1;
        TTL_NANO = ttl;
    }

    @Deprecated
    public StimulusModel getModel() {
        return model;
    }

    @Deprecated
    public int claimSingleStimulus(int stimulusType, int targetId, int targetType, float value, long expiry) {
        // TODO
        return 0;
    }

    @Deprecated
    public int claimMatrixStimulus(int eventType, int targetIndex, int targetType, long expiry) {
        // TODO
        return 0;
    }

    @Deprecated
    public int claimMultiDataStimulus(int eventType, int transferType, int targetRef) {
        long now = System.nanoTime(); // TODO check
        int start = nextSearchStart.getAndAdd(32) & MASK;

        for (int i = 0; i < 64; i++) {
            int index = (start + i) & MASK;

            if (model.getExpiry(index) < now) {
                if (model.tryWriteLock(index)) {
                    try {
                        if (model.getExpiry(index) < now) {

                            model.setStimulusType(index, eventType);
                            model.setTargetRef(index, targetRef);
                            //model.setValue(index, value);
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

    @Deprecated
    public void invalidate(int index) {
        try {
            long now = System.nanoTime(); // TODO check
            model.writeLock(index);
            model.setExpiry(index, now - TTL_NANO);
        } finally {
            model.writeUnlock(index);
        }
    }

    @Deprecated
    public float getValue(int index) {
        return model.getValue(index);
    }

    @Deprecated
    public int getTargetType(int index) {
        return model.getTargetType(index);
    }

    @Deprecated
    public int getStimulusType(int index) {
        return model.getStimulusType(index);
    }

    @Deprecated
    public int getTransferType(int index) {
        return model.getTransferType(index);
    }

    @Deprecated
    public long getExpiry(int index) {
        return model.getExpiry(index);
    }

    @Deprecated
    public int getTargetRef(int index) {
        return model.getTargetRef(index);
    }
}
