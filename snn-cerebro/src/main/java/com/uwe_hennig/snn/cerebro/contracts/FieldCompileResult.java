/// @(#)FieldCompileResult.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

import java.util.List;
import java.util.Optional;

import com.uwe_hennig.snn.anatomy.allocator.NeuronFieldModelManager;

/// FieldCompileResult
///
/// @author Uwe Hennig
///
public interface FieldCompileResult {
    String toDot(String comment);
    int totalBytes(int fieldBuffer, int neighboursBuffer);

    int maxNeighbours();
    int numFields();

    boolean isSuccess();
    List<String> getErrorMessage();

    // returns field id
    Optional<NeuronFieldModelManager> build(int fieldBuffer, int neighboursBuffer);
}
