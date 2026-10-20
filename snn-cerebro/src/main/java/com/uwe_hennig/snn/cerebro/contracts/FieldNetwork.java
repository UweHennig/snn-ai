/// @(#)FieldNetwork.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

import com.uwe_hennig.snn.cerebro.field.NeuronField;

/// FieldNetwork
///
/// @author Uwe Hennig
///
public interface FieldNetwork {
    String toDot(String comment);
    long totalBytes();

    NeuronField build();
}
