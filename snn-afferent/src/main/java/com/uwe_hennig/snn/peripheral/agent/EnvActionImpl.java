/**
 * @(#)EnvActionImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.peripheral.agent;

import com.uwe_hennig.snn.contracts.peripheral.ActionConsumer;
import com.uwe_hennig.snn.contracts.peripheral.EnvAction;
import com.uwe_hennig.snn.contracts.peripheral.EnvSignal;

/**
 * EnvActionImpl
 *
 * @author Uwe Hennig
 */
public class EnvActionImpl implements EnvAction {
    private final long           identifier;

    private ActionConsumer consumer;

    private EnvActionImpl(long identifier) {
        this.identifier = identifier;
    }

    public static <A> EnvAction of(long identifier, ActionConsumer consumer) {
        return new EnvActionImpl(identifier);
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
    public void withConsumer(ActionConsumer consumer) {
        this.consumer = consumer;
    }
}
