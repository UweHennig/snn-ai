/**
 * @(#)GraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

import java.util.List;

/**
 * GraphGenerator
 *
 * @author Uwe Hennig
 */
public interface GraphGenerator {
    /*
     * The 'generate' method extends the initial graph with further edges and returns these as a graph.
     */
    List<Graph> generate(GenerationContext context, List<Graph> initialGraph);
}
