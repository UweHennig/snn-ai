/// @(#)ScaleFreeGraph.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.graph.generator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.uwe_hennig.snn.graph.generator.contracts.IdGenerator;
import com.uwe_hennig.snn.graph.generator.contracts.Node;


/// ScaleFreeGraph Barabási-Albert-Algorithmus
///
/// @author Uwe Hennig
///
public class ScaleFreeGraph {
    /**
     * Generiert einen skalenfreien Graphen nach dem Barabási-Albert-Modell.
     *
     * @param initialNodes
     *            Number of start nodes (m0)
     * @param maxNodes
     *            Desired total number of nodes (N)
     * @param edgesPerNewNode
     *            Number of edges associated with a new node (m <= initialNodes)
     * @param defaultFieldType
     *            Default value for the fieldType attribute
     * @param defaultMaxNeurons
     *            Default value for the maxNeurons attribute
     * @return A set of all the Node objects created
     */
    public static Set<Node> generateGraph(IdGenerator idGen, int initialNodes, int maxNodes, int edgesPerNewNode, int defaultFieldType, int defaultMaxNeurons) {
        if (edgesPerNewNode > initialNodes) {
            throw new IllegalArgumentException("edgesPerNewNode (m) must not be greater than initialNodes (m0).");
        }
        if (initialNodes > maxNodes) {
            throw new IllegalArgumentException("initialNodes must not be greater than maxNodes.");
        }

        Random random = new Random();

        Map<Integer, Set<Integer>> inMap = new HashMap<>();
        Map<Integer, Set<Integer>> outMap = new HashMap<>();

        for (int i = 0; i < maxNodes; i++) {
            inMap.put(i, new HashSet<>());
            outMap.put(i, new HashSet<>());
        }

        List<Integer> degreePool = new ArrayList<>();

        // -------------------------------------------------------------
        // Step 1: Initial core graph (fully connected m0 nodes)
        // -------------------------------------------------------------
        for (int i = 0; i < initialNodes; i++) {
            for (int j = i + 1; j < initialNodes; j++) {
                addEdge(i, j, inMap, outMap, degreePool);
            }
        }

        // -------------------------------------------------------------
        // Step 2: Growth & preferred connectivity
        // -------------------------------------------------------------
        for (int newNode = initialNodes; newNode < maxNodes; newNode++) {
            Set<Integer> chosenTargets = new HashSet<>();

            while (chosenTargets.size() < edgesPerNewNode) {
                int randomIndex = random.nextInt(degreePool.size());
                int targetNode = degreePool.get(randomIndex);

                if (targetNode != newNode) {
                    chosenTargets.add(targetNode);
                }
            }

            for (int target : chosenTargets) {
                addEdge(newNode, target, inMap, outMap, degreePool);
            }
        }

        // -------------------------------------------------------------
        // Step 3: Generation of the final, unalterable node records
        // -------------------------------------------------------------
        Set<Node> graph = new HashSet<>();
        for (int i = 0; i < maxNodes; i++) {
            graph.add(new Node(i, defaultFieldType, Collections.unmodifiableSet(inMap.get(i)), Collections.unmodifiableSet(outMap.get(i))));
        }

        return graph;
    }

    /**
     * Fügt eine ungerichtete Kante hinzu (symmetrisch in inNeighbours und outNeighbours). Hinweis: Wenn Sie gerichtete
     * Kanten wünschen (z.B. neu -> alt), tragen Sie es nur gerichtet ein.
     */
    private static void addEdge(int u, int v, Map<Integer, Set<Integer>> inMap, Map<Integer, Set<Integer>> outMap, List<Integer> degreePool) {
        // Kante u <-> v (ungerichtet modelliert in in/out)
        outMap.get(u).add(v);
        inMap.get(v).add(u);

        outMap.get(v).add(u);
        inMap.get(u).add(v);

        // Beide Knoten kommen für jede Kante in den Pool
        degreePool.add(u);
        degreePool.add(v);
    }

    // -------------------------------------------------------------
    // Test / Anwendungsbeispiel
    // -------------------------------------------------------------
    public static void main(String[] args) {
        int initialNodes = 3;
        int maxNodes = 10;
        int edgesPerNewNode = 3;
        Set<Node> graph = generateGraph(IdGenerator.ID_GENERATOR, initialNodes, maxNodes, edgesPerNewNode, 1, 100);

        // Zur Demonstration: Finde die größten Hubs
        System.out.println("Graph generiert mit " + graph.size() + " Knoten.");
        System.out.println("\nTop 5 Hubs (Knoten mit den meisten Verbindungen):");

        graph.stream()
            .sorted((a, b) -> Integer.compare(b.outNeighbours().size(), a.outNeighbours().size()))
            .forEach(node -> System.out.println("Knoten-ID: " + node.id() + " | Grad (Connections): " + node.outNeighbours().size()));

        for (Node node : graph) {
            for (Integer id : node.outNeighbours()) {
                System.out.println("" + node.id() + " -> " + id);
            }
        }
    }
}
