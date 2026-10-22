/// @(#)GraphType.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.contracts.graph;

/// GraphType
///
/// @author Uwe Hennig
/// @formatter:off
public sealed interface GraphType
        permits GraphType.Random, GraphType.Ring, GraphType.LayeredGrid, GraphType.SmallWorld,
                GraphType.Tube, GraphType.Fractal, GraphType.ScaleFree {
    /// A random graph, but connected
    record Random(int nodes) implements  GraphType {}

    /// Simple ring
    record Ring(int nodes) implements  GraphType {}

    /// Spinal cord
    record Tube(int nodes, int depth)  implements  GraphType {}

    /// not yet definined
    record Fractal(int nodes, int depth) implements  GraphType {}

    /// Cortical layers I–VI
    record LayeredGrid(int width, int height, int layers) implements GraphType {}

    /// Barabási–Albert
    record ScaleFree(int nodes, int edgesPerNewNode) implements GraphType {}

    /// Watts-Strogatz
    record SmallWorld(int nodes, int neighbours, float rewireProbability) implements GraphType {}

    static GraphType RANDOM(int fields, int maxNeurons) {
        return new Random(fields);
    }

    static GraphType RING(int nodes) {
        return new Ring(nodes);
    }

    static GraphType LAYERD_GRID(int width, int height,int layers) {
        return new LayeredGrid(width, height, layers);
    }

    static GraphType TUBE(int ringNodes, int depth) {
        return new Tube(ringNodes, depth);
    }

    static GraphType FRACTAL(int nodes, int depth) {
        return new Fractal(nodes, depth);
    }

    static GraphType SCALE_FREE(int nodes, int edgesPerNewNode) {
        return new ScaleFree(nodes, edgesPerNewNode);
    }

    static GraphType SMALL_WORLD(int nodes, int neighbours, float rewireProbability) {
        return new SmallWorld(nodes, neighbours, rewireProbability);
    }
}
///  @formatter:on