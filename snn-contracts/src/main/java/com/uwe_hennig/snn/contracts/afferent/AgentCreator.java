/**
 * @(#)AgentCreator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.afferent;

/**
 * AgentCreator
 * Build instructions for AgentMediator
 *
 * @author Uwe Hennig
 */
public interface AgentCreator<T extends AgentCreator<T>> {
    @SuppressWarnings("unchecked")
    default T create() {
        loadServices();
        createNeuronField();
        createConnectome();
        createEnvironment();

        // SnnEffector -> EnvAction
        createEffectorMapping();

        // EnvState -> SnnReceptor
        createStateMapping();

        // EnvFeedback -> SnnFeedabck
        createFeedbackMapping();

        return (T) this;
    }

    // Step 1
    void loadServices();

    // Step 2
    void createNeuronField();

    // Step 3
    void createConnectome();

    // Step 4
    void createEnvironment();

    // Step 5
    void createEffectorMapping();

    // Step 6
    void createStateMapping();

    // Step 7
    void createFeedbackMapping();
}
