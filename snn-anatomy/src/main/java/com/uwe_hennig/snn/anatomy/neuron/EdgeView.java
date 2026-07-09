/**
 * @(#)EdgeView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import com.uwe_hennig.snn.anatomy.allocator.EdgeModelManager;

/**
 * EdgeView
 *
 * @author Uwe Hennig
 */
public class EdgeView {
    // ----- Domain Logic -----

    public static void setSingleEdge(int index, int srcId, int srcType, int trgId, int trgType) {
        EdgeModel model = EdgeModelManager.instance().getModel();
        try {
            model.writeLock(index);
            model.setSrcId(index, srcId);
            model.setSrcType(index, srcType);
            model.setSingleTrgRef(index, trgId);
            model.setTrgType(index, trgType);
        } finally {
            model.writeUnlock(index);
        }
    }

    public static void setMultiEdge(int index, int srcId, int srcType, int trgRef, int trgType) {
        EdgeModel model = EdgeModelManager.instance().getModel();
        try {
            model.writeLock(index);
            model.setSrcId(index, srcId);
            model.setSrcType(index, srcType);
            model.setMultiTrgRef(index, -1);
            model.setTrgType(index, trgType);
        } finally {
            model.writeUnlock(index);
        }
    }

    public static boolean isMultiTargetRef(int index) {
        EdgeModel model = EdgeModelManager.instance().getModel();
        return model.isMuliTrgRef(index);
    }

    public static int getSrcId(int index) {
        EdgeModel model = EdgeModelManager.instance().getModel();
        return model.getSrcId(index);
    }

    public static int getSrcType(int index) {
        EdgeModel model = EdgeModelManager.instance().getModel();
        return model.getSrcType(index);
    }

    public static int getTrgType(int index) {
        EdgeModel model = EdgeModelManager.instance().getModel();
        return model.getTrgType(index);
    }

    public static int getTrgRef(int index) {
        EdgeModel model = EdgeModelManager.instance().getModel();
        return model.getTrgRef(index);
    }
}
