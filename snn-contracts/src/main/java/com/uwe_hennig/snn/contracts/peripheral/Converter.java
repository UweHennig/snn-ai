/**
 * @(#)Converter.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * Converter
 * translates the SNN-RL communication
 * @author Uwe Hennig
 */
public interface Converter<T, R> {
    R convert(T input);
}
