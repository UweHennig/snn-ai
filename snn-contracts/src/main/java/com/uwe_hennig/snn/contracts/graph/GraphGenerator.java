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
@FunctionalInterface
public interface GraphGenerator {
    void generate(IdProvider ids, GraphListener listener);
}
