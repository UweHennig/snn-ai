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
    private final int [] EMPTY_ARRAY = new int[0];

    private final MultiList multiList;
    private final long      nodeRef;
    private final int       nodeId;

    private long metaRef     = -1;
    private long parentsRef  = -1;
    private long childrenRef = -1;
    private long neuronsRef  = -1;

    public FieldNode(int nodeId, MultiList multiList) {
        this.multiList = multiList;
        this.nodeRef = multiList.allocate();
        this.nodeId = nodeId;
        this.metaRef = multiList.allocate();
    }

    public long addParentIds(int... parentIds) {
        if (parentsRef == -1) {
            parentsRef = multiList.allocate();
            updateMetaRef();
        }

        updateIdentifiers(parentsRef, parentIds);

        return parentsRef;
    }

    public long addChildIds(int... childIds) {
        if (childrenRef == -1) {
            childrenRef = multiList.allocate();
            updateMetaRef();
        }
        updateIdentifiers(childrenRef, childIds);

        return childrenRef;
    }

    public long addNeuronIds(int ... neuronIds) {
        if (neuronsRef == -1) {
            neuronsRef = multiList.allocate();
            updateMetaRef();
        }
        updateIdentifiers(neuronsRef, neuronIds);

        return neuronsRef;
    }

    private void updateIdentifiers(long ref, int... values) {
        int[] existing = multiList.getInts(ref);
        int[] updated = new int[existing.length + values.length];

        System.arraycopy(existing, 0, updated, 0, existing.length);
        System.arraycopy(values, 0, updated, existing.length, values.length);

        multiList.put(ref, updated);
    }

    public int getNodeId() {
        return nodeId;
    }

    public long getNodeRef() {
        return nodeRef;
    }

    public long getParentsRef() {
        return parentsRef;
    }

    public long getChildrenRef() {
        return childrenRef;
    }

    public int[] getParentIds() {
        if (parentsRef != -1) {
            return multiList.getInts(parentsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public int[] getChildIds() {
        if (childrenRef != -1) {
            return multiList.getInts(childrenRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    public int[] getNeuronIds() {
        if (neuronsRef != -1) {
            return multiList.getInts(neuronsRef);
        } else {
            return EMPTY_ARRAY;
        }
    }

    private void updateMetaRef() {
        long[] metaArray = { parentsRef, childrenRef, neuronsRef };
        multiList.put(metaRef, metaArray);
    }
}
