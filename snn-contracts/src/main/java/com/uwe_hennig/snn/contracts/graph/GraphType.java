/// @(#)GraphType.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.contracts.graph;

/// GraphType
///
/// @author Uwe Hennig
///
public sealed interface GraphType
        permits GraphType.Random, GraphType.Ring, GraphType.LayeredGrid, GraphType.SmallWorld,
                GraphType.Tube, GraphType.Fractal, GraphType.ScaleFree {
    record Random(int nodes) implements  GraphType {}
    record Ring(int nodes) implements  GraphType {}
    record LayeredGrid(int width, int height, int layers) implements GraphType {}
    record Tube(int ringNodes, int depth)  implements  GraphType {}
    record Fractal(int depth) implements  GraphType {}
    record ScaleFree(int nodes, int edgesPerNewNode) implements GraphType {}
    record SmallWorld(int nodes, int neighbours, float rewireProbability) implements GraphType {}

    static GraphType RANDOM(int nodes) {
        return new Random(nodes);
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

    static GraphType FRACTAL(int depth) {
        return new Fractal(depth);
    }

    static GraphType SCALE_FREE(int nodes, int edgesPerNewNode) {
        return new ScaleFree(nodes, edgesPerNewNode);
    }

    static GraphType SMALL_WORLD(int nodes, int neighbours, float rewireProbability) {
        return new SmallWorld(nodes, neighbours, rewireProbability);
    }
}
