/**
 * @(#)SnnBitSetTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * SnnBitSetTest
 * @author Uwe Hennig
 */
public class SnnBitSetTest {
    @Test
    @DisplayName("Simple BitSet test")
    public void testSimpleBitSet() {
        try (SnnBitSet bs = new SnnBitSet(1)) {
            bs.set(3);
            bs.set(1_000_00);
            boolean flag3 = bs.get(3);
            assertEquals(true, flag3);

            boolean falg4 = bs.get(5);
            assertEquals(false, falg4);

            boolean flag1Mio = bs.get(1_000_00);
            assertEquals(true, flag1Mio);

            boolean flag10Mio = bs.get(10_000_00);
            assertEquals(false, flag10Mio);

        } catch (Exception e) {
            fail("Exception in testSimpleBitSet " + e.getLocalizedMessage());
        }
    }
}
