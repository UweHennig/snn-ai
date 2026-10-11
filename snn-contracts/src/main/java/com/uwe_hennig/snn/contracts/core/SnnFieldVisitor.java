/// @(#)SnnFieldVisitor.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.contracts.core;

/// SnnFieldVisitor
///
/// @author Uwe Hennig
///
@FunctionalInterface
public interface SnnFieldVisitor {
    /// @return true, to abort the traversal; false to continue.
    boolean visit(int fieldId);
}
