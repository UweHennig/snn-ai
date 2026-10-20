/**
 * @(#)NeuronFieldBuilder.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.contracts;

import com.uwe_hennig.snn.contracts.graph.GraphType;

/**
 * NeuronFieldBuilder
 * @author Uwe Hennig
 */
public interface NeuronFieldBuilder {
    // Declare structure
    NeuronFieldBuilder addReceptorFields(GraphType type, int maxNeuronCount);
    NeuronFieldBuilder addEffectorFields(GraphType type, int maxNeuronCount);
    NeuronFieldBuilder addFeedbackFields(GraphType type, int maxNeuronCount);
    NeuronFieldBuilder addAssociativeFields(GraphType type, int maxNeuronCount);

    // Transition: Validate
    FieldNetwork compile();
}