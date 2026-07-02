/**
 * @(#)ViewIdentity.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.core;

/**
 * ViewIdentity
 *
 * @author Uwe Hennig
 */
public abstract class ViewIdentity {
    public abstract int getViewId();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ViewIdentity other)) {
            return false;
        }
        return this.getViewId() == other.getViewId();
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(this.getViewId());
    }
}
