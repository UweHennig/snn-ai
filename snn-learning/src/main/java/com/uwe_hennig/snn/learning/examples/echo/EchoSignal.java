/**
 * @(#)EchoSignal.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.learning.examples.echo;

import com.uwe_hennig.snn.contracts.peripheral.EnvSignal;

/**
 * EchoSignal
 *
 * @author Uwe Hennig
 */
public record EchoSignal(Integer data) implements EnvSignal<Integer> {
}
