/// @(#)SmallWorldTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.graph.genrator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

/// SmallWorldTest
///
/// @author Uwe Hennig
public class SmallWorldTest {
    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
