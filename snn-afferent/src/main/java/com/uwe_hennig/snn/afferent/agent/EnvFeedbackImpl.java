/**
 * @(#)EnvFeedbackImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.afferent.agent;

import com.uwe_hennig.snn.contracts.afferent.EnvFeedback;
import com.uwe_hennig.snn.contracts.afferent.EnvFeedbackType;
import com.uwe_hennig.snn.contracts.afferent.EnvSignal;
import com.uwe_hennig.snn.contracts.afferent.FeedbackConsumer;

/**
 * EnvFeedbackImpl
 *
 * @author Uwe Hennig
 */
public class EnvFeedbackImpl implements EnvFeedback {
    private final long            identifier;
    private final EnvFeedbackType type;
    private FeedbackConsumer      consumer;

    private EnvFeedbackImpl(long identifier, EnvFeedbackType type) {
        this.identifier = identifier;
        this.type = type;
    }

    public static EnvFeedback of(long identifier, EnvFeedbackType type) {
        return new EnvFeedbackImpl(identifier, type);
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
    public void withConsumer(FeedbackConsumer consumer) {
        this.consumer = consumer;
    }

    @Override
    public EnvFeedbackType getFeedbackType() {
        return type;
    }

}
