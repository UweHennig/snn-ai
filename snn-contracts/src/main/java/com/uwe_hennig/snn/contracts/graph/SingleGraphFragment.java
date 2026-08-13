/**
 * @(#)SingleGraphFragment.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.graph;

import java.util.List;

/**
 * SingleGraphFragment
 *
 * @author Uwe Hennig
 */
public interface SingleGraphFragment {
    List<Edge> edges();
    SingleGraphFragment addEdge(Edge edge);
    SingleGraphFragment addAllEdges(List<Edge> edge);

    int sizeEdges();
    int sizeNodes();
}
