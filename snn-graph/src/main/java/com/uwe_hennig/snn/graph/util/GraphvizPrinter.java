/// @(#)GraphvizPrinter.java
///  Copyright (c) 2026 Uwe Hennig
///  All rights reserved.
package com.uwe_hennig.snn.graph.util;

import java.util.Set;

import com.uwe_hennig.snn.graph.generator.contracts.Node;

/// GraphvizPrinter
///
/// @author Uwe Hennig
///
public class GraphvizPrinter {
    private GraphvizPrinter() {
    }

    public static String toDot(String comment, Set<Node> receptorFields, Set<Node> effectorFields, Set<Node> associativeFields,
        Set<Node> feedbackFields) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph SNN {\n");
        builder.append("// " + comment + "\n");
        builder.append(" compound=true;\n");
        builder.append(" node [shape=none];\n");
        builder.append(" edge [arrowhead=empty, color=red];\n");
        builder.append(" layout = dot;\n");

        builder.append(" subgraph cluster_receptor {\n");
        builder.append("\t label = \"receptor\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_receptor [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (Node node : receptorFields) {
            builder.append(node.id() + ";\n");
        }
        builder.append(" }\n");

        builder.append(" subgraph cluster_associative {\n");
        builder.append("\t label = \"associative\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_associative [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (Node node : associativeFields) {
            builder.append(node.id() + ";\n");
        }
        builder.append(" }\n");

        builder.append(" subgraph cluster_feedback {\n");
        builder.append("\t label = \"feedback\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_feedback [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (Node node : feedbackFields) {
            builder.append(node.id() + ";\n");
        }
        builder.append(" }\n");

        builder.append(" subgraph cluster_effector {\n");
        builder.append("\t label = \"effector\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_effector [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (Node node : effectorFields) {
            builder.append(node.id() + ";\n");
        }
        builder.append(" }\n");

        // connect subgraphs
        builder.append(" anchor_receptor:s    -> anchor_associative:n [ltail=cluster_receptor, lhead=cluster_associative, color=blue];\n");
        builder.append(" anchor_associative:s ->  anchor_effector:n   [ltail=cluster_associative, lhead=cluster_effector, color=blue];\n");
        builder.append(" anchor_feedback:s    -> anchor_receptor:n    [ltail=cluster_feedback, lhead=cluster_receptor, color=blue];\n");
        builder.append(" anchor_feedback:s    -> anchor_associative:n [ltail=cluster_feedback, lhead=cluster_associative, color=blue];\n");
        builder.append(" anchor_feedback:s    -> anchor_effector:n    [ltail=cluster_feedback, lhead=cluster_effector, color=blue];\n");

        // connections
        for(Node node: receptorFields) {
            for (int target: node.outNeighbours()) {
                builder.append(node.id()).append(" -> ").append(target).append(";\n");
            }
        }
        for(Node node: effectorFields) {
            for (int target: node.outNeighbours()) {
                builder.append(node.id()).append(" -> ").append(target).append(";\n");
            }
        }
        for(Node node: associativeFields) {
            for (int target: node.outNeighbours()) {
                builder.append(node.id()).append(" -> ").append(target).append(";\n");
            }
        }
        for(Node node: feedbackFields) {
            for (int target: node.outNeighbours()) {
                builder.append(node.id()).append(" -> ").append(target).append(";\n");
            }
        }

        builder.append("}\n");

        return builder.toString();
    }

}
