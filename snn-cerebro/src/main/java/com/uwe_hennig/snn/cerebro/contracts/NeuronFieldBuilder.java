/**
 * @(#)NeuronFieldBuilder.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.contracts;

/**
 * NeuronFieldBuilder
 *
 * @author Uwe Hennig
 */
public interface NeuronFieldBuilder {
    // 1. Declare fields
    NeuronFieldBuilder addReceptorFields(int countFields, int maxNeuronCount);
    NeuronFieldBuilder addEffectorFields(int countFields, int maxNeuronCount);
    NeuronFieldBuilder addFeedbackFields(int countFields, int maxNeuronCount);
    NeuronFieldBuilder addAssociativeFields(int countFields, int maxNeuronCount);

    // 2. Projection rules
    NeuronFieldBuilder wireReceptors(int outDegree);
    NeuronFieldBuilder wireEffectors(int inDegree);
    NeuronFieldBuilder wireAssociative(int inDegree, int outDegree);
    NeuronFieldBuilder wireFeedback(int inDegree, int outDegree);

    // 3. Transition: Validate
    FieldNetwork compile();

}