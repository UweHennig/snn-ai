/**
 * @(#)SnnFeedbackImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.peripheral.agent;

import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.contracts.peripheral.InformationFilter;
import com.uwe_hennig.snn.contracts.peripheral.SnnFeedback;
import com.uwe_hennig.snn.contracts.peripheral.TemporalFilter;

/**
 * SnnFeedbackImpl
 *
 * @author Uwe Hennig
 */
public class SnnFeedbackImpl implements SnnFeedback {
    private final int          identifier;
    private final StimulusType stimulusType;
    private final int          relateDendritId;

    private TemporalFilter    temporalFilter    = (_, _) -> true;
    private InformationFilter informationFilter = _ -> true;

    private long lastEmit = 0L;

    private SnnFeedbackImpl(int identifier, StimulusType stimulusType, int relatedDendritId) {
        this.identifier = identifier;
        this.stimulusType = stimulusType;
        this.relateDendritId = relatedDendritId;
    }

    public static SnnFeedback of(int identifier, StimulusType stimulusType, int relatedDendritId) {
        return new SnnFeedbackImpl(identifier, stimulusType, relatedDendritId);
    }

    public void setTemporalFilter(TemporalFilter filter) {
        this.temporalFilter = filter;
    }

    public void setInformationFilter(InformationFilter filter) {
        this.informationFilter = filter;
    }

    @Override
    public void perceive(float value) {
        if (temporalFilter.allow(System.nanoTime(), lastEmit) && informationFilter.allow(value)) {
// TODO
//            int stimulusId = StimulusService.claim(stimulusType.code(), value, relateDendritId);
//            SnnTransferservice.transfer(stimulusId);
//            lastEmit = System.nanoTime();
        }
    }

    @Override
    public StimulusType getType() {
        return stimulusType;
    }

    public int getIdentifier() {
        return identifier;
    }
}
