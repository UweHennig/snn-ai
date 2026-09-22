/**
 * @(#)ReceptorView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import com.uwe_hennig.snn.util.BufferedTransferSegment;

/**
 * ReceptorView
 *
 * @author Uwe Hennig
 */
public final class ReceptorView {
    private final BufferedTransferSegment model;
    private final int                     index;
    private final int                     entries;

    @FunctionalInterface
    public interface TransferAction {
        void offer(int index, int entry, float value);
    }

    public ReceptorView(BufferedTransferSegment model, int index) {
        this.model = model;
        this.index = index;
        this.entries = model.getEntries(index);
    }

    public int getIndex() {
        return index;
    }

    public void transferStimulus(float[][] values) {
        transfer(values, model::offerStimulus);
    }

    public void transferFbValue(float[][] values) {
        transfer(values, model::offerFbValue);
    }

    public void transferFbTime(float[][] values) {
        transfer(values, model::offerFbTime);
    }

    public void transferStimulus(float value) {
        model.offerStimulus(index, 0, value);
    }

    public void transferFbTime(float value) {
        model.offerFbTime(index, 0, value);
    }

    public void transferFbValue(float value) {
        model.offerFbValue(index, 0, value);
    }

    public void transferStimulus(float [] values) {
        transfer(values, model::offerStimulus);
    }

    public void transferFbTime(float [] values) {
        transfer(values, model::offerFbTime);
    }

    public void transferFbValue(float [] values) {
        transfer(values, model::offerFbValue);
    }

    private void transfer(float[][] values, TransferAction action) {
        int entry = 0;
        for (int r = 0; r < values.length; r++) {
            float[] row = values[r];
            for (int c = 0; c < row.length; c++) {
                if (entry >= entries) {
                    return;
                }
                action.offer(index, entry++, row[c]);
            }
        }
    }

    private void transfer(float[] values, TransferAction action) {
        int entry = 0;
        for (int c = 0; c < values.length; c++) {
            if (entry >= entries) {
                return;
            }
            action.offer(index, entry++, values[c]);
        }
    }
}
