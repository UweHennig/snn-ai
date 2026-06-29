/**
 * @(#)SnnEffectorImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.peripheral.agent;

import com.uwe_hennig.snn.contracts.peripheral.EffectorConsumer;
import com.uwe_hennig.snn.contracts.peripheral.SnnEffector;
import com.uwe_hennig.snn.services.StimulusService;

/**
 * SnnEffectorImpl
 *
 * @author Uwe Hennig
 */
public class SnnEffectorImpl implements SnnEffector {
    private final int identifier;

    private EffectorConsumer consumer;

    private SnnEffectorImpl(int identifier) {
        this.identifier = identifier;
    }

    public static SnnEffector of(int identifier) {
        return new SnnEffectorImpl(identifier);
    }

    public void stimulate(int stimulusIdentifier) {
        if (consumer != null) {
            float value = StimulusService.getValue(stimulusIdentifier);
            consumer.accept(this, value);
        }
    }

    @Override
    public long getIdentifier() {
        return identifier;
    }

    @Override
    public void withConsumer(EffectorConsumer consumer) {
        this.consumer = consumer;
    }
}
