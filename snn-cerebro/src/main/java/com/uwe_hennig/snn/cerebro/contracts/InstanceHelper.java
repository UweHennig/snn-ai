/// @(#)InstanceHelper.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

/// InstanceHelper
///
/// @author Uwe Hennig
///
final class InstanceHelper {
    private InstanceHelper() {}

    static <T> T create(Class<T> contract, String className) {
        try {
            ClassLoader cl = contract.getClassLoader();
            Class<?> rawClass = Class.forName(className, true, cl);

            return contract.cast(rawClass.getDeclaredConstructor().newInstance());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Implementation not found: " + className, e);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to instantiate class: " + className, e);
        }
    }
}
