/// @(#)SmallWorld.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.graph.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.uwe_hennig.snn.graph.generator.contracts.IdGenerator;
import com.uwe_hennig.snn.graph.generator.contracts.Node;

/// SmallWorld WattsStrogatz
///
/// @author Uwe Hennig
///
public class SmallWorld {
    public static Set<Node> generateGraph(IdGenerator idGen, int type, int countNodes, int stepSizeControl, double exchangePct) {
        if (countNodes < 3) {
            throw new IllegalArgumentException("Count nodes must be greater than or equal to 3!");
        }

        if (stepSizeControl < 1 || stepSizeControl > countNodes / 2) {
            throw new IllegalArgumentException("stepSizeControl must be between 1 and countNodes / 2!");
        }

        Node[] nodes = new Node[countNodes];
        for (int i = 0; i < countNodes; i++) {
            nodes[i] = new Node(idGen.apply(), type, new LinkedHashSet<>(), new LinkedHashSet<>());
        }

        // Step 1: initial ring
        for (int i = 0; i < countNodes; i++) {
            for (int step = 1; step <= stepSizeControl; step++) {
                int j = (i + step) % countNodes;
                nodes[i].addOutNeighbour(nodes[j].id());
                nodes[j].addInNeighbour(nodes[i].id());
            }
        }

        // Step 2: Rewiring
        Random random = new Random();

        for (int i = 0; i < countNodes; i++) {
            // Performance-Shortcut
            if (nodes[i].outNeighbours().size() >= countNodes - 1) {
                continue;
            }

            List<Integer> targetsToProcess = new ArrayList<>();
            for (int step = 1; step <= stepSizeControl; step++) {
                targetsToProcess.add((i + step) % countNodes);
            }

            for (int j : targetsToProcess) {
                if (random.nextDouble() < exchangePct) {
                    if (nodes[j].inNeighbours().size() <= 1) {
                        continue;
                    }

                    int maxLoops = countNodes * 2;
                    int w = 0;
                    boolean found = false;

                    while (maxLoops > 0) {
                        w = random.nextInt(countNodes);
                        maxLoops--;

                        if (w != i && w != j && !nodes[i].hasOutNeighbour(w)) {
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        nodes[i].removeOutNeighbour(nodes[j].id());
                        nodes[j].removeInNeighbour(nodes[i].id());

                        nodes[i].addOutNeighbour(nodes[w].id());
                        nodes[w].addInNeighbour(nodes[i].id());
                    }
                }
            }
        }

        return new LinkedHashSet<>(Arrays.asList(nodes));
    }
}
