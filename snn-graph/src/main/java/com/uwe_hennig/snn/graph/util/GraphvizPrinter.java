/**
 * @(#)GraphvizPrinter.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.graph.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import com.uwe_hennig.snn.contracts.graph.Edge;
import com.uwe_hennig.snn.contracts.graph.GenerationContext;
import com.uwe_hennig.snn.contracts.graph.SingleGraphFragment;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/**
 * GraphvizPrinter
 *
 * @author Uwe Hennig
 */
public class GraphvizPrinter {
    public static final SNNLogger log = new SNNLogger();

    public static void printGraph(GenerationContext context, String comment) {
        log.debug(() -> renderToString(context, comment));
    }

    public static void rewriteGraphFile(String filename, GenerationContext context, String comment) {
        try {
            String result = renderToString(context, comment);
            Path path = getPath(filename);
            Files.writeString(path, result, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String renderToString(GenerationContext context, String comment) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph SNN {\n");
        builder.append("// " + comment + "\n");
        SingleGraphFragment graph = context.completeGraph();

        if (graph != null && graph.edges().size() < 350) {
            builder.append("node [shape=none];\n").append("layout = neato;\n").append("edge [arrowhead=empty color=red];\n");

            for (Edge edge : graph.edges()) {
                if (context.isEdgeMarked(edge.edgeId())) {
                    builder.append(edge).append("\n");
                } else {
                    builder.append(edge).append(" [color=green]").append("\n");
                }
            }
        } else {
            builder.append("node [label=\".\", shape=none];\n").append("layout = fdp;\n").append("edge [arrowhead=empty color=gray dir=none];\n");

            for (Edge edge : graph.edges()) {
                builder.append(edge).append("\n");
            }
        }

        builder.append("}\n");
        return builder.toString();
    }

    private static Path getPath(String relativePath) {
        String basisPfad = System.getProperty("snn.dir");

        if (basisPfad == null) {
            return Paths.get(System.getProperty("user.dir"), "src", "test", "resources", relativePath);
        }

        return Paths.get(basisPfad, "snn", "resources", relativePath);
    }
}
