/// @(#)FieldNetwork.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

import java.nio.file.Path;

import com.uwe_hennig.snn.cerebro.field.NeuronField;

/// FieldNetwork
///
/// @author Uwe Hennig
///
public interface FieldNetwork {
    void toDot(Path path, String comment);
    long totalBytes();

    NeuronField build();
}
