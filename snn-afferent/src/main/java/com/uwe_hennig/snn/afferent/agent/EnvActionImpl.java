/**
 * @(#)EnvActionImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.afferent.agent;

import com.uwe_hennig.snn.contracts.afferent.ActionConsumer;
import com.uwe_hennig.snn.contracts.afferent.EnvAction;
import com.uwe_hennig.snn.contracts.afferent.EnvSignal;

/**
 * EnvActionImpl
 *
 * @author Uwe Hennig
 */
public class EnvActionImpl<A> implements EnvAction<A> {
    private final long           identifier;

    private A action;
    private ActionConsumer consumer;

    private EnvActionImpl(long identifier, A action) {
        this.identifier = identifier;
        this.action = action;
    }

    public static <A> EnvAction<A> of(long identifier, A action, ActionConsumer consumer) {
        return new EnvActionImpl<A>(identifier, action);
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
    public A getData() {
        return action;
    }

    @Override
    public void withConsumer(ActionConsumer consumer) {
        this.consumer = consumer;
    }
}
