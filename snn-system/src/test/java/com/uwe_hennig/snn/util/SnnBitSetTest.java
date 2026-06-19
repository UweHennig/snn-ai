/**
 * @(#)SnnBitSetTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * SnnBitSetTest
 *
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
            e.printStackTrace();
            fail("Exception in testSimpleBitSet " + e.getLocalizedMessage());
        }
    }

    @Test
    @DisplayName("Simple SnnMultiBitSet test")
    public void testMultiBitSet() {
        final int numFields = 2;
        final int numTypes  = 2;

        SnnMultiBitSet mbs = new SnnMultiBitSet(numFields, numTypes);
        mbs.set(0, 0, 1);
        checkValue(mbs, true, 0, 0, 1);

        mbs.set(0, 1, 2);
        checkValue(mbs, true, 0, 1, 2);

        mbs.set(1, 0, 3);
        checkValue(mbs, true, 1, 0, 3);

        mbs.set(1, 1, 4);
        checkValue(mbs, true, 1, 1, 4);

        mbs.unset(0, 0, 1);
        checkValue(mbs, false, 0, 0, 1);

        mbs.unset(0, 1, 2);
        checkValue(mbs, false, 0, 1, 2);

        mbs.unset(1, 0, 3);
        checkValue(mbs, false, 1, 0, 3);

        mbs.unset(1, 1, 4);
        checkValue(mbs, false, 1, 1, 4);
    }

    private void checkValue(SnnMultiBitSet mbs, boolean expected, int field, int type, int index) {
        boolean result = mbs.get(field, type, index);
        assertEquals(expected, result, "invalid value in SnnMultiBitSet!");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
