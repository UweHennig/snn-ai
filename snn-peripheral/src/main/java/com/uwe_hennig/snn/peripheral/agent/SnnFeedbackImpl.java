/**
 * @(#)SnnFeedbackImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.peripheral.agent;

import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.contracts.peripheral.SnnFeedback;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * SnnFeedbackImpl
 *
 * @author Uwe Hennig
 */
public class SnnFeedbackImpl implements SnnFeedback {
    private final int          identifier;
    private final StimulusType stimulusType;
    private final int          relateDendritId;

    private SnnFeedbackImpl(int identifier, StimulusType stimulusType, int relatedDendritId) {
        this.identifier = identifier;
        this.stimulusType = stimulusType;
        this.relateDendritId = relatedDendritId;
    }

    public static SnnFeedback of(int identifier, StimulusType stimulusType, int relatedDendritId) {
        return new SnnFeedbackImpl(identifier, stimulusType, relatedDendritId);
    }

    @Override
    public void perceive(float value) {
        int stimulusId = StimulusService.claim(stimulusType.code(), value, relateDendritId);
        SnnTransferservice.transfer(stimulusId);
    }

    @Override
    public StimulusType getType() {
        return stimulusType;
    }

    public int getIdentifier() {
        return identifier;
    }
}
