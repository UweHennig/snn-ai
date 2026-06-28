/**
 * @(#)SnnReceptorImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.afferent.agent;

import static com.uwe_hennig.snn.contracts.core.NeuronElementType.DENDRIT;
import static com.uwe_hennig.snn.contracts.core.StimulusType.STIMULUS;

import com.uwe_hennig.snn.contracts.afferent.SnnReceptor;
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * SnnReceptorImpl
 *
 * @author Uwe Hennig
 */
public class SnnReceptorImpl implements SnnReceptor {
    private final int identifier;
    private final int relatedNeuronElementId;

    private SnnReceptorImpl(int identifier, int relatedNeuronElementId) {
        this.identifier = identifier;
        this.relatedNeuronElementId = relatedNeuronElementId;
    }

    public static SnnReceptor of(int identifier, int relatedNeuronElementId, StimulusType stimulusType) {
        return new SnnReceptorImpl(identifier, relatedNeuronElementId);
    }

    @Override
    public void perceive(float value) {
        int stimulusId = StimulusService.claim(STIMULUS.code(), identifier, relatedNeuronElementId, -1, DENDRIT.code(), value);
        SnnTransferservice.transfer(stimulusId, DENDRIT.code());
    }
}
