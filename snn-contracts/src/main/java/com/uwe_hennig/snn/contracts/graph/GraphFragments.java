/**
 * @(#)GraphFragments.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

import java.util.List;

/**
 * GraphFragments
 *
 * @author Uwe Hennig
 */
public interface GraphFragments {
    List<SingleGraphFragment> fragments();
    SingleGraphFragment meld();

    GraphFragments addFragement(SingleGraphFragment component);
    GraphFragments addEdge(int fragmentIdx, Edge edge);
}
