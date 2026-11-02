/// @(#)FieldCompileResult.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

import java.util.Optional;

import com.uwe_hennig.snn.cerebro.field.NeuronField;

/// FieldCompileResult
///
/// @author Uwe Hennig
///
public interface FieldCompileResult {
    String toDot(String comment);
    long totalBytes();

    boolean isSuccess();
    String getErrorMessage();

    Optional<NeuronField> build();
}
