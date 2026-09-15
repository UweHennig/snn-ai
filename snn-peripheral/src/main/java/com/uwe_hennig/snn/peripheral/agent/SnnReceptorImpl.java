/**
 * @(#)SnnReceptorImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.peripheral.agent;

import com.uwe_hennig.snn.anatomy.peripheral.ReceptorView;
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.contracts.peripheral.SnnReceptor;
import com.uwe_hennig.snn.contracts.peripheral.TemporalFilter;

/**
 * SnnReceptorImpl
 *
 * @author Uwe Hennig
 */
public final class SnnReceptorImpl implements SnnReceptor {
    private final int          identifier;
    private final ReceptorView view;
    private TemporalFilter     temporalFilter; // TODO implementation required

    private SnnReceptorImpl(int identifier, ReceptorView view) {
        this.identifier = identifier;
        this.view = view;
    }

    public void setTemporalFilter(TemporalFilter filter) {
        this.temporalFilter = filter;
    }

    @Override
    public void perceive(StimulusType stimulusType, float[][] values) {
        switch (stimulusType) {
            case STIMULUS:
                view.transferStimulus(values);
            break;
            case TIME_FEEDBACK:
                view.transferFbTime(values);
            break;
            case VALUE_FEEDBACK:
                view.transferFbValue(values);
            break;
        }
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
