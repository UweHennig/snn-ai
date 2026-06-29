/**
 * @(#)FeedbackConsumer.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * FeedbackConsumer
 *
 * @author Uwe Hennig
 */
@FunctionalInterface
public interface FeedbackConsumer {
    void accept(EnvFeedback envFeedback, EnvSignal<?> envSignal);
}
