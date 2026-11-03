/// @(#)Node.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.graph.generator.contracts;

import java.util.ArrayList;
import java.util.List;

/// Node
///
/// @author Uwe Hennig
///
public record Node(int id, int nodeType, List<Integer> neighbours) {
    public static Node of(int id, int type) {
        return new Node(id, type, new ArrayList<>());
    }
    public Node addNeighbour(int neighbourId) {
        neighbours.add(neighbourId);
        return this;
    }
}
