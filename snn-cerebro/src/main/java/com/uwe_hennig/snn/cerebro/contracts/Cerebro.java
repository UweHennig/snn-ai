/// @(#)Cerebro.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

import java.util.List;

/// Cerebro
///
/// @author Uwe Hennig
///
public interface Cerebro {
    List<Integer> getReceptorPorts();
    List<Integer> getTimeFeedbackPorts();
    List<Integer> getValueFeedbackPorts();
    List<Integer> getEffectorPorts();
}
