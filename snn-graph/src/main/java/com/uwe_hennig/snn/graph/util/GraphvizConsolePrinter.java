/**
 * @(#)GraphvizConsolePrinter.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.util;

import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/**
 * GraphvizConsolePrinter
 *
 * @author Uwe Hennig
 */
public class GraphvizConsolePrinter {
    public static final SNNLogger log = new SNNLogger();

    public static void printGraph(GenerationContext context, String comment, SingleGraphFragment graph) {
        log.debug(() -> renderToString(context, comment, graph));
    }

    private static String renderToString(GenerationContext context, String comment, SingleGraphFragment graph) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph SNN {\n");
        builder.append("// " + comment + "\n");

        if (graph != null && graph.edges().size() < 350) {
            builder
                .append("node [shape=none];\n")
                .append("layout = neato;\n")
                .append("edge [arrowhead=empty color=red];\n");

            for (Edge edge : graph.edges()) {
                if (context.isEdgeMarked(edge.edgeId())) {
                    builder.append(edge).append("\n");
                } else {
                    builder.append(edge).append(" [color=green]").append("\n");
                }
            }
        } else {
            builder
                .append("node [label=\".\", shape=none];\n")
                .append("layout = fdp;\n")
                .append("edge [arrowhead=empty color=gray dir=none];\n");

            for (Edge edge : graph.edges()) {
                builder.append(edge).append("\n");
            }
        }

        builder.append("}\n");
        return builder.toString();
    }
}
