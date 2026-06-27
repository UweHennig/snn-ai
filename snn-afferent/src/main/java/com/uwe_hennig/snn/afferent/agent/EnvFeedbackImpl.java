/**
 * @(#)EnvFeedbackImpl.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.afferent.agent;

import com.uwe_hennig.snn.contracts.afferent.EnvFeedback;
import com.uwe_hennig.snn.contracts.afferent.EnvSignal;
import com.uwe_hennig.snn.contracts.afferent.FeedbackConsumer;

/**
 * EnvFeedbackImpl
 *
 * @author Uwe Hennig
 */
public class EnvFeedbackImpl<F> implements EnvFeedback<F> {
    private final long identifier;
    private F          feedback;

    private FeedbackConsumer consumer;

    private EnvFeedbackImpl(long identifier, F feedback) {
        this.identifier = identifier;
        this.feedback = feedback;
    }

    public static <F> EnvFeedback<F> of(long identifier, F feedback) {
        return new EnvFeedbackImpl<F>(identifier, feedback);
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
    public F getData() {
        return feedback;
    }

    @Override
    public void withConsumer(FeedbackConsumer consumer) {
        this.consumer = consumer;
    }

}
