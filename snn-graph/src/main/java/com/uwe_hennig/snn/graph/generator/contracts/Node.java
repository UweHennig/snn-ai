/// @(#)Node.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.graph.generator.contracts;

import java.util.LinkedHashSet;
import java.util.Set;

/// Node
///
/// @author Uwe Hennig
///
public record Node(int fieldType, int id, Set<Integer> inNeighbours, Set<Integer> outNeighbours, int maxNeurons) {

    public static Node of(int fieldType, int id, int maxNeurons) {
        return new Node(fieldType, id, new LinkedHashSet<Integer>(), new LinkedHashSet<Integer>(), maxNeurons);
    }

    public Node addInNeighbour(int id) {
        inNeighbours.add(id);
        return this;
    }

    public Node addOutNeighbour(int id) {
        outNeighbours.add(id);
        return this;
    }

    public Node removeOutNeighbour(int id) {
        outNeighbours.remove(id);
        return this;
    }

    public Node removeInNeighbour(int id) {
        inNeighbours.remove(id);
        return this;
    }

    public boolean hasOutNeighbour(int id) {
        return outNeighbours.contains(id);
    }
}
