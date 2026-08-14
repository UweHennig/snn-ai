/**
 * @(#)SnnReceptorImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.peripheral.agent;

import static com.uwe_hennig.snn.contracts.core.StimulusType.STIMULUS;

import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.contracts.peripheral.InformationFilter;
import com.uwe_hennig.snn.contracts.peripheral.SnnReceptor;
import com.uwe_hennig.snn.contracts.peripheral.TemporalFilter;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * SnnReceptorImpl
 *
 * @author Uwe Hennig
 */
public class SnnReceptorImpl implements SnnReceptor {
    private final int identifier;
    private final int relatedDendritId;

    private TemporalFilter    temporalFilter    = (_, _) -> true;
    private InformationFilter informationFilter = _ -> true;

    private long lastEmit = 0L;

    private SnnReceptorImpl(int identifier, int relatedDendritId) {
        this.identifier = identifier;
        this.relatedDendritId = relatedDendritId;
    }

    public static SnnReceptor of(int identifier, int relatedNeuronElementId, StimulusType stimulusType) {
        return new SnnReceptorImpl(identifier, relatedNeuronElementId);
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
            int stimulusId = StimulusService.claim(STIMULUS.code(), value, relatedDendritId);
            SnnTransferservice.transfer(stimulusId);
            lastEmit = System.nanoTime();
        }
    }

    public int getIdentifier() {
        return identifier;
    }
}
