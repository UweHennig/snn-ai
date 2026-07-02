/**
 * @(#)FieldGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.contracts;

import java.util.List;

import com.uwe_hennig.snn.cerebro.field.NeuronField;

/**
 * FieldGraph
 *
 * @author Uwe Hennig
 */
public record FieldGraph(
    List<NeuronField> afferent,
    List<NeuronField> associative,
    List<NeuronField> efferent,
    List<NeuronField> feedback) {
}
