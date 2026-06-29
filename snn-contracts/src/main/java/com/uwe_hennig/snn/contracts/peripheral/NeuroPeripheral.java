/**
 * @(#)Peripheral.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

import java.util.List;

/**
 * Peripheral
 *
 * @author Uwe Hennig
 */
public interface NeuroPeripheral {
    public List<SnnReceptor> getReceptors();
    public List<SnnEffector> getEffectors();
    public List<SnnFeedback> getFeedbacks();
}
