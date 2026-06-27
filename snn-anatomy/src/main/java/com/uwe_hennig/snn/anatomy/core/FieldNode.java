/**
 * @(#)FieldNode.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;

/**
 * FieldNode
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
        updateMetaRef();
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
        node.addInNeighborsRef(this.nodeRef);
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

    public void addInNeighbors(FieldNode node) {
        addInNeighborsRef(node.nodeRef);
        node.addOutNeighborsRef(this.nodeRef);
    }

    public long addInNeighborsRef(long... inRefs) {
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

    public long[] getNeuronIds() {
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
        long[] updated = add(existing, values);

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

    private static long[] add(long[] existing, long... values) {
        // case 1
        if (values.length == 0) {
            long[] result = existing.clone();
            Arrays.sort(result);
            return result;
        }

        // case 2
        if (values.length == 1) {
            long singleValue = values[0];

            for (long val : existing) {
                if (val == singleValue) {
                    long[] result = existing.clone();
                    Arrays.sort(result);
                    return result;
                }
            }
            long[] result = new long[existing.length + 1];
            System.arraycopy(existing, 0, result, 0, existing.length);
            result[existing.length] = singleValue;
            Arrays.sort(result);
            return result;
        }

        // case 3
        long[] aSorted = existing.clone();
        Arrays.sort(aSorted);

        int newElementsCount = 0;
        for (int i = 0; i < values.length; i++) {
            if (Arrays.binarySearch(aSorted, values[i]) >= 0) {
                values[i] = -1;
            } else {
                newElementsCount++;
            }
        }

        long[] result = new long[existing.length + newElementsCount];
        System.arraycopy(aSorted, 0, result, 0, existing.length);

        int targetIndex = existing.length;
        for (int i = 0; i < values.length; i++) {
            if (values[i] != -1) {
                result[targetIndex] = values[i];
                targetIndex++;
            }
        }

        Arrays.sort(result);
        return result;
    }
}
