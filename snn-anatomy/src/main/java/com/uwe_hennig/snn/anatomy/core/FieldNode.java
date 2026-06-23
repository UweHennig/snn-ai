/**
 * @(#)FieldGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

/**
 * FieldGraph
 *
 * @author Uwe Hennig
 */
public class FieldNode {
    private final long[] EMPTY_ARRAY = new long[0];

    private final MultiList multiList;
    private final long      viewId;

    private long nodeRef         = -1;
    private long inNeighborsRef  = -1;
    private long outNeighborsRef = -1;
    private long neuronsRef      = -1;

    public FieldNode(long viewId, MultiList multiList) {
        this.multiList = multiList;

        this.viewId = viewId;
        this.nodeRef = multiList.allocate();
    }

    public long getViewId() {
        return viewId;
    }

    public long getNodeId() {
        return nodeRef;
    }

    // BFS
    public static void visit(FieldNode startNode, MultiList ml, Consumer<FieldNode> visitor) {
        List<Long> visited = new ArrayList<>();
        Deque<Long> queue = new ArrayDeque<>();
        queue.add(startNode.nodeRef);

        while (!queue.isEmpty()) {
            long currentRef = queue.poll();
            if (!visited.contains(currentRef)) {
                visited.add(currentRef);
                FieldNode node = createWrapper(currentRef, ml);
                visitor.accept(node);
                for (long outRef : node.getNeighborsRef()) {
                    queue.add(outRef);
                }
            }
        }
    }

    // --- Out Neighbors ---

    public void addOutNeighbors(FieldNode node) {
        addOutNeighborsRef(node.nodeRef);
    }

    public long addOutNeighborsRef(long ... outRefs) {
        if (outNeighborsRef == -1) {
            outNeighborsRef = multiList.allocate();
            updateMetaRef();
        }
        updateIdentifiers(outNeighborsRef, outRefs);

        return outNeighborsRef;
    }

    public long[] getOutNeighborsRef() {
        if (outNeighborsRef != -1) {
            return multiList.getLongs(outNeighborsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    // --- In Neighbors ---

    public void addInNeighborsNode(FieldNode node) {
        addInNeighborsRef(node.nodeRef);
    }

    public long addInNeighborsRef(long ... inRefs) {
        if (inNeighborsRef == -1) {
            inNeighborsRef = multiList.allocate();
            updateMetaRef();
        }
        updateIdentifiers(inNeighborsRef, inRefs);

        return inNeighborsRef;
    }

    public long[] getInNeighborsRef() {
        if (inNeighborsRef != -1) {
            return multiList.getLongs(inNeighborsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    // --- Neurons ---

    public long addNeuronId(long... neuronIds) {
        if (neuronsRef == -1) {
            neuronsRef = multiList.allocate();
            updateMetaRef();
        }
        updateIdentifiers(neuronsRef, neuronIds);

        return neuronsRef;
    }

    public long[] getNeuronRefs() {
        if (neuronsRef != -1) {
            return multiList.getLongs(neuronsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    // --- Convenience ---

    private long[] getNeighborsRef() {
        long[] childs = (outNeighborsRef != -1) ? multiList.getLongs(outNeighborsRef) : EMPTY_ARRAY;
        long[] parents = (inNeighborsRef != -1) ? multiList.getLongs(inNeighborsRef) : EMPTY_ARRAY;

        long[] both = new long[childs.length + parents.length];

        System.arraycopy(childs, 0, both, 0, childs.length);
        System.arraycopy(parents, 0, both, childs.length, parents.length);

        return both;
    }

    private void updateIdentifiers(long ref, long... values) {
        long[] existing = multiList.getLongs(ref);
        long[] updated = new long[existing.length + values.length];

        System.arraycopy(existing, 0, updated, 0, existing.length);
        System.arraycopy(values, 0, updated, existing.length, values.length);

        multiList.put(ref, updated);
    }

    private void updateMetaRef() {
        long[] metaArray = { viewId, inNeighborsRef, outNeighborsRef, neuronsRef };
        multiList.put(nodeRef, metaArray);
    }

    private static long[] getMetaRefs(long nodeRef, MultiList ml) {
        return ml.getLongs(nodeRef);
    }

    private static FieldNode createWrapper(long nodeRef, MultiList multiList) {
        long[] meta = getMetaRefs(nodeRef, multiList);
        FieldNode node = new FieldNode(meta[0], multiList);
        node.nodeRef = nodeRef;
        node.inNeighborsRef = meta[1];
        node.outNeighborsRef = meta[2];
        node.neuronsRef = meta[3];
        return node;
    }

}
