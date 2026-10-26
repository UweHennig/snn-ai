/// @(#)IdGenerator.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.graph.generator.contracts;

import java.util.concurrent.atomic.AtomicInteger;

/// IdGenerator
///
/// @author Uwe Hennig
///
@FunctionalInterface
public interface IdGenerator {
    IdGenerator ID_GENERATOR = IdGenerator.sequence(0);

    int apply();

    static IdGenerator sequence(int startValue) {
        AtomicInteger counter = new AtomicInteger(startValue);
        return counter::getAndIncrement;
    }
}
