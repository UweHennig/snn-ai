/**
 * @(#)SnnReceptorImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.peripheral.agent;

import com.uwe_hennig.snn.anatomy.allocator.ReceptorModelManager;
import com.uwe_hennig.snn.contracts.peripheral.SnnReceptor;
import com.uwe_hennig.snn.contracts.peripheral.TemporalFilter;

/**
 * SnnReceptorImpl
 *
 * @author Uwe Hennig
 */
public final class SnnReceptorImpl implements SnnReceptor {
    private final int identifier;
    private TemporalFilter temporalFilter; // TODO implementation required

    private SnnReceptorImpl(int identifier) {
        this.identifier = identifier;
    }

    public void setTemporalFilter(TemporalFilter filter) {
        this.temporalFilter = filter;
    }

    @Override
    public void perceive(float [][] value) {
        // TODO
    }

    public int getIdentifier() {
        return identifier;
    }

    // TODO implementation required
    private float getIntakeDistance() {
        return ReceptorModelManager.instance()
            .getRecptorView(identifier)
            .getIntakeDistance(identifier);
    }
}
