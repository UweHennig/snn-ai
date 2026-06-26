/**
 * @(#)EnvFeedback.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.afferent;

/**
 * EnvFeedback
 *
 * @author Uwe Hennig
 */
public interface EnvFeedback<F> {
    long getIdentifier();
    void setConsumer(FeedbackConsumer agentConsumer);
}
