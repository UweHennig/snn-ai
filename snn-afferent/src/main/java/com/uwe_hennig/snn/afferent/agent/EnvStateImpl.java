/**
 * @(#)EnvStateImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.afferent.agent;

import com.uwe_hennig.snn.contracts.afferent.EnvSignal;
import com.uwe_hennig.snn.contracts.afferent.EnvState;
import com.uwe_hennig.snn.contracts.afferent.StateConsumer;

/**
 * EnvStateImpl
 *
 * @author Uwe Hennig
 */
public class EnvStateImpl implements EnvState {
    private final long identifier;

    private StateConsumer consumer;

    private EnvStateImpl(long identifier) {
        this.identifier = identifier;
    }

    public static EnvState of(long identifier) {
        return new EnvStateImpl(identifier);
    }

    @Override
    public long getIdentifier() {
        return identifier;
    }

    @Override
    public void invoke(EnvSignal<?> signal) {
        if (consumer != null) {
            consumer.accept(this, signal);
        }
    }

    @Override
    public void withConsumer(StateConsumer consumer) {
        this.consumer = consumer;
    }
}
