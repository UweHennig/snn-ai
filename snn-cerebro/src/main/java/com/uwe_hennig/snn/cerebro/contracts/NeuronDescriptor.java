/// @(#)NeuronDescriptor.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

/// NeuronDescriptor
///
/// @author Uwe Hennig
///
public record NeuronDescriptor(int numDendrites, int numSynapses) {
    public NeuronDescriptor {
        if (numDendrites < 0 || numSynapses < 0) {
            throw new IllegalArgumentException("Counts must be non-negative");
        }
    }
}
