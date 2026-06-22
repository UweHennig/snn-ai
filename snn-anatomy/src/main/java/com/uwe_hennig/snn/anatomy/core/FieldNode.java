/**
 * @(#)FieldGraph.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

/**
 * FieldGraph
 *
 * @author Uwe Hennig
 */
public class FieldNode {
    private final MultiList multiList;
    private final long      nodeRef;
    private final int       nodeId;

    private long metaRef     = -1;
    private long parentRef   = -1;
    private long childrenRef = -1;
    private long neuronRef   = -1;

    public FieldNode(int nodeId, MultiList multiList) {
        this.multiList = multiList;
        this.nodeRef = multiList.allocate();
        this.nodeId = nodeId;
        this.metaRef = multiList.allocate();
    }

    public long addParentIds(int... parentIds) {
        if (parentRef == -1) {
            parentRef = multiList.allocate();
            updateMetaRef();
        }
        int[] parents = multiList.getInts(parentRef);
        int[] updated = new int[parents.length + parentIds.length];

        System.arraycopy(parents, 0, updated, 0, parents.length);
        System.arraycopy(parentIds, 0, updated, parents.length, parentIds.length);

        multiList.put(parentRef, updated);

        return parentRef;
    }

    public int getNodeId() {
        return nodeId;
    }

    public long getNodeRef() {
        return nodeRef;
    }

    public long getParentsRef() {
        return parentRef;
    }

    public long getChildrenRef() {
        return childrenRef;
    }

    public int[] getParentIds() {
        return multiList.getInts(parentRef);
    }

    private void updateMetaRef() {
        long [] metaArray = {parentRef, childrenRef, neuronRef};
        multiList.put(metaRef, metaArray);
    }
}
