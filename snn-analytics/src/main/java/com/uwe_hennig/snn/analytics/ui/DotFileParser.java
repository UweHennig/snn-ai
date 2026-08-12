/**
 * @(#)DotFileParser.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.analytics.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * DotFileParser
 *
 * @author Uwe Hennig
 */
public class DotFileParser {
    public static final class Node {
        int        id;
        double     x;
        double     y;
        List<Node> neighbors;

        private Node(int id, double x, double y, List<Node> neighbors) {
            this.id = id;
            this.x = x;
            this.y = y;
            this.neighbors = neighbors;
        }

        public static Node of(int nodeId, double xPos, double yPos) {
            return new Node(nodeId, xPos, yPos, new ArrayList<>());
        }

        public boolean isConnected(Node node) {
            return neighbors.contains(node);
        }

        public void addConnection(Node node) {
            neighbors.add(node);
        }

        public static Node of(int nodeId) {
            return new Node(nodeId, randPos(), randPos(), new ArrayList<>());
        }

        private static double randPos() {
            return Math.random() * 100.0;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Node node = (Node) o;
            return id == node.id;
        }

        @Override
        public int hashCode() {
            return Integer.hashCode(id);
        }
    }

    public static List<Node> parse(Path dotFile) throws IOException, NumberFormatException {
        assert dotFile != null : "File is null!";

        List<Node> result = new ArrayList<>();

        for (String line : Files.readAllLines(dotFile)) {
            line = line.trim();

            if (!line.contains("->")) {
                continue;
            }

            int attrIndex = line.indexOf('[');
            if (attrIndex != -1) {
                line = line.substring(0, attrIndex).trim();
            }

            line = line.replace(";", "").trim();

            String[] parts = line.split("->");
            if (parts.length != 2) {
                continue;
            }

            int fromNodeId = Integer.parseInt(parts[0].trim());
            int toNodeId = Integer.parseInt(parts[1].trim());

            Node fromNode = result.stream().filter(n -> n.id == fromNodeId).findFirst().orElseGet(() -> {
                Node nn = Node.of(fromNodeId);
                result.add(nn);
                return nn;
            });

            Node toNode = result.stream().filter(n -> n.id ==toNodeId).findFirst().orElseGet(() -> {
                Node nn = Node.of(toNodeId);
                result.add(nn);
                return nn;
            });

            if (!fromNode.isConnected(toNode)) {
                fromNode.addConnection(toNode);
            }

        }

        return result;
    }

    public static List<Node> parse(String path) throws IOException {
        return parse(Paths.get(path));
    }
}
