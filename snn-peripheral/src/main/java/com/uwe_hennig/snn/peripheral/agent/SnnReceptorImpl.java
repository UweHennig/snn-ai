/**
 * @(#)SnnReceptorImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.peripheral.agent;

import com.uwe_hennig.snn.anatomy.allocator.ReceptorModelManager;
import com.uwe_hennig.snn.anatomy.peripheral.ReceptorView;
import com.uwe_hennig.snn.contracts.peripheral.SnnReceptor;
import com.uwe_hennig.snn.contracts.peripheral.TemporalFilter;

/**
 * SnnReceptorImpl
 *
 * @author Uwe Hennig
 */
public final class SnnReceptorImpl implements SnnReceptor {
    private final int      identifier;
    private TemporalFilter temporalFilter; // TODO implementation required

    private SnnReceptorImpl(int identifier) {
        this.identifier = identifier;
    }

    public void setTemporalFilter(TemporalFilter filter) {
        this.temporalFilter = filter;
    }

    @Override
    public void perceive(float[][] value) {
        // TODO
        ReceptorView view = ReceptorModelManager.instance().getRecptorView(identifier);
        int rows = view.getNumRows();
        int cols = view.getNumColumns();

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // TODO
                //view.setValue(r, c, value[r][c]);
            }
        }

        // TODO view.publishBlock(tapeId);
        // TODO Event
        // new Event(RECEPTOR, tapeId, ??);
    }

    public int getIdentifier() {
        return identifier;
    }

    // TODO implementation required
    private float getIntakeDistance() {
        //return ReceptorModelManager.instance().getRecptorView(identifier).getIntakeDistance(identifier);
        return 0.0f;
    }
}
