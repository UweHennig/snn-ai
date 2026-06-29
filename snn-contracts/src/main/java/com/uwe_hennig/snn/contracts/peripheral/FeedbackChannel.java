/**
 * @(#)FeedbackChannel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * FeedbackChannel
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class FeedbackChannel {
    private final EnvFeedback envFeedback;
    private final Converter<EnvSignal<?>, Float> converter;
    private final SnnFeedback snnFeedback;

    public FeedbackChannel(EnvFeedback envFeedback, Converter<EnvSignal<?>, Float> converter, SnnFeedback snnFeedback) {
        this.envFeedback = envFeedback;
        this.converter = converter;
        this.snnFeedback = snnFeedback;
    }

    public EnvFeedback getEnvFeedback() {
        return envFeedback;
    }

    public Converter<EnvSignal<?>, Float> getConverter() {
        return converter;
    }

    public SnnFeedback getSnnFeedback() {
        return snnFeedback;
    }

    public long getIdentifier() {
        return envFeedback.getIdentifier();
    }
}
