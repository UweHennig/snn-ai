/**
 * @(#)ActionChannel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.afferent;

/**
 * ActionChannel
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class ActionChannel {
    private final EnvAction<?> action;
    private final SnnEffector effector;

    private final Converter<Float, EnvSignal<?>> actionConverter;

    public ActionChannel(SnnEffector effector, Converter<Float, EnvSignal<?>> actionConverter, EnvAction<?> action) {
        this.action = action;
        this.actionConverter = actionConverter;
        this.effector = effector;
    }

    public EnvAction<?> getAction() {
        return action;
    }

    public Converter<Float, EnvSignal<?>> getConverter() {
        return actionConverter;
    }

    public SnnEffector getEffector() {
        return effector;
    }

    public long getIdentifier() {
        return effector.getIdentifier();
    }
}
