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
public class EnvStateImpl<S> implements EnvState<S> {
    private final long identifier;

    private S             state;
    private StateConsumer consumer;

    private EnvStateImpl(long identifier, S state) {
        this.identifier = identifier;
        this.state = state;
    }

    public static <S> EnvState<S> of(long identifier, S state) {
        return new EnvStateImpl<S>(identifier, state);
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
    public S getData() {
        return state;
    }

    @Override
    public void withConsumer(StateConsumer consumer) {
        this.consumer = consumer;
    }
}
