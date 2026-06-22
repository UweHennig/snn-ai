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

    private long nodeRef     = -1;
    private long parentsRef  = -1;
    private long childrenRef = -1;
    private long neuronsRef  = -1;

    public FieldNode(long viewId, MultiList multiList) {
        this.multiList = multiList;

        this.viewId = viewId;
        this.nodeRef = multiList.allocate();
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
                for (long outRef : node.getOutRefs()) {
                    queue.add(outRef);
                }
            }
        }
    }

    // TODO ERROR!!
    public void addParentNode(FieldNode node) {
        addParentRefs(node.nodeRef);
    }

    public void addChildNode(FieldNode node) {
        addChildRefs(node.nodeRef);
    }

    public long addParentRefs(long... parentRefs) {
        if (parentsRef == -1) {
            parentsRef = multiList.allocate();
            updateMetaRef();
        }

        updateIdentifiers(parentsRef, parentRefs);

        return parentsRef;
    }

    public long addChildRefs(long... childRefs) {
        if (childrenRef == -1) {
            childrenRef = multiList.allocate();
            updateMetaRef();
        }
        updateIdentifiers(childrenRef, childRefs);

        return childrenRef;
    }

    public long addNeuronId(long... neuronIds) {
        if (neuronsRef == -1) {
            neuronsRef = multiList.allocate();
            updateMetaRef();
        }
        updateIdentifiers(neuronsRef, neuronIds);

        return neuronsRef;
    }

    public long getViewId() {
        return viewId;
    }

    public long getNodeId() {
        return nodeRef;
    }

    public long getParentsRef() {
        return parentsRef;
    }

    public long getChildrenRef() {
        return childrenRef;
    }

    public long[] getParentRefs() {
        if (parentsRef != -1) {
            return multiList.getLongs(parentsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public long[] getChildRefs() {
        if (childrenRef != -1) {
            return multiList.getLongs(childrenRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public long[] getNeuronRefs() {
        if (neuronsRef != -1) {
            return multiList.getLongs(neuronsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public long[] getOutRefs() {
        long[] childs = (childrenRef != -1) ? multiList.getLongs(childrenRef) : EMPTY_ARRAY;
        long[] parents = (parentsRef != -1) ? multiList.getLongs(parentsRef) : EMPTY_ARRAY;

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
        long[] metaArray = { viewId, parentsRef, childrenRef, neuronsRef };
        multiList.put(nodeRef, metaArray);
    }

    private static long[] getMetaRefs(long nodeRef, MultiList ml) {
        return ml.getLongs(nodeRef);
    }

    private static FieldNode createWrapper(long nodeRef, MultiList multiList) {
        long[] meta  = getMetaRefs(nodeRef, multiList);
        FieldNode node = new FieldNode(meta[0], multiList);
        node.nodeRef = nodeRef;
        node.parentsRef = meta[1];
        node.childrenRef = meta[2];
        node.neuronsRef = meta[3];
        return node;
    }

}
