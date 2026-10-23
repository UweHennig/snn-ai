/// @(#)SmallWorld.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.graph.generator;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import com.uwe_hennig.snn.graph.generator.contracts.Node;

/// SmallWorld WattsStrogatz
///
/// @author Uwe Hennig
///
public class SmallWorld {
    public static Set<Node> generateGraph(int startId, int type, int maxNeurons, int countNodes, int stepSizeControl, double exchangePct) {
        if (countNodes < 3) {
            throw new IllegalArgumentException("Count nodes must be greater than or equal to 3!");
        }

        if (stepSizeControl > countNodes / 2) {
            throw new IllegalArgumentException("stepSizeControl must be less than half the number of nodes!");
        }

        Node[] nodes = new Node[countNodes];
        for (int i = 0; i < countNodes; i++) {
            nodes[i] = new Node(type, i, new LinkedHashSet<>(), new LinkedHashSet<>(), maxNeurons);
        }

        // Step 1: initial ring
        for (int i = 0; i < countNodes; i++) {
            for (int step = 1; step <= stepSizeControl; step++) {
                int j = (i + step) % countNodes;
                nodes[i].addOutNeighbour(j);
                nodes[j].addInNeighbour(i);
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
                        nodes[i].removeOutNeighbour(j);
                        nodes[j].removeInNeighbour(i);

                        nodes[i].addOutNeighbour(w);
                        nodes[w].addInNeighbour(i);
                    }
                }
            }
        }

        // relative identifier
        LinkedHashSet<Node> result = new LinkedHashSet<>();
        for(Node node: nodes) {
            Node newNode = Node.of(type, startId + node.id(), maxNeurons);
            for (Integer id : node.outNeighbours()) {
                newNode.addOutNeighbour(startId + id);
            }
            for (Integer id : node.inNeighbours()) {
                newNode.addInNeighbour(startId + id);
            }
            result.add(newNode);
        }
        return result;
    }
}
