/// @(#)NeuronInfo.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

/// NeuronInfo
///
/// @author Uwe Hennig
///
public record NeuronInfo(int fieldId, int neuronId, int somaId, int axonId, int[] dendriteIds, int[] synapseIds) {
}
