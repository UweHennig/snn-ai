/**
 * @(#)DefaultAfferentGraphGenerator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph;

import java.util.List;

import com.uwe_hennig.snn.contracts.graph.Graph;
import com.uwe_hennig.snn.contracts.graph.GraphGenerator;
import com.uwe_hennig.snn.contracts.graph.GraphListener;
import com.uwe_hennig.snn.contracts.graph.IdProvider;

/**
 * DefaultAfferentGraphGenerator
 *
 * @author Uwe Hennig
 */
public class DefaultAfferentGraphGenerator implements GraphGenerator {

    @Override
    public List<Graph> generate(IdProvider ids, GraphListener listener, Graph initialGraph) {
        // TODO
        return List.of();
    }
}
