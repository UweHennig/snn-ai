/// @(#)FieldStage.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.contracts.graph.GraphType;

/// FieldStage
///
/// @author Uwe Hennig
///
public interface FieldStage {
    // 1. Set fields
    FieldStage withField(NeuronFieldType fieldType, int numFields, int edges, float probability);

    // 2. Connection parameters between the fields
    FieldStage connectReceptor(int forwardConnections);
    FieldStage connectAssociative(int forwardConnections, int backwardsConnections);
    FieldStage connectEffector(int backwardsConnections);
    FieldStage connectFeedback(int forwardConnections);

    // 3. Neural structures for a field type
    FieldStage withNeurons(NeuronFieldType type, GraphType graphType, NeuronDescriptor neuronDescriptor);

    // 4. Check the result
    CompileResult compile();

    // 5. Reset in case of errors
    FieldStage reset();

    // 7. Activating NeuronStage
    CerebroBuilder build(int fieldBuffer, int neighboursBuffer);
}
