/// @(#)NeuronFieldBuilder.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

/// NeuronFieldBuilder
/// uses Small World by Watts-Strogatz
///
/// @author Uwe Hennig
///
public interface NeuronFieldBuilder {
    NeuronFieldBuilder withReceptorFields(int numFiedlds, int edges, float probability);
    NeuronFieldBuilder withAssociativeFields(int numFiedlds, int edges, float probability);
    NeuronFieldBuilder withEffectorFields(int numFiedlds, int edges, float probability);
    NeuronFieldBuilder withFeedbackFields(int numFiedlds, int edges, float probability);

    NeuronFieldBuilder connectReceptor(int forwardConnections);
    NeuronFieldBuilder connectAssociative(int forwardConnections, int backwardsConnections);
    NeuronFieldBuilder connectEffector(int backwardsConnections);
    NeuronFieldBuilder connectFeedback(int forwardConnections);

    FieldCompileResult compile();
}