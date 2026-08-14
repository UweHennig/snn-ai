/**
 * @(#)InformationFilter.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * InformationFilter
 * The InformationFilter discards signals that are considered invalid in terms of their content.
 * An example would be values that are either too high or too low.
 * @author Uwe Hennig
 */
public interface InformationFilter {
    boolean allow(float value);
}
