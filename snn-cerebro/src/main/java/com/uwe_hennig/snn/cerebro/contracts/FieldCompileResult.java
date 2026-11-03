/// @(#)FieldCompileResult.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

import java.util.List;
import java.util.Optional;

import com.uwe_hennig.snn.cerebro.field.NeuronField;

/// FieldCompileResult
///
/// @author Uwe Hennig
///
public interface FieldCompileResult {
    String toDot(String comment);
    long totalBytes();

    int maxNeighbours();
    int numFields();

    boolean isSuccess();
    List<String> getErrorMessage();

    Optional<NeuronField> build();
}
