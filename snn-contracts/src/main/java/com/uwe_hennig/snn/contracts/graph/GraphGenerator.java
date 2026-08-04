/**
 * @(#)GraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

/**
 * GraphGenerator
 *
 * @author Uwe Hennig
 */
public interface GraphGenerator {
    // The 'generate' method extends the initial graph with further edges and returns these as a graph.
    GraphFragments generate(GenerationContext context, SingleGraphFragment graph);

    // The 'generate' method create an inital graph
    SingleGraphFragment generate(GenerationContext context);
}
