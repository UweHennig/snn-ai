/// @(#)NeuronFieldModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;

/// NeuronFieldModelManager
///
/// @author Uwe Hennig
///
public final class NeuronFieldModelManager {
    private static final String            FILENAME = "field.snn";
    private static NeuronFieldModelManager INSTANCE;

    private NeuronFieldModel model;

    private NeuronFieldModelManager(int size) {
        model = new NeuronFieldModel(size);
    }

    public static NeuronFieldModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (NeuronFieldModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronFieldModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public int claimField(int maxNeurons, int maxOutNeighbours, int maxInNeighbours, int maxData) {
        return NeuronFieldModel.calculateSize(maxNeurons, maxOutNeighbours, maxInNeighbours, maxData);
    }

    public NeuronFieldModel getModel() {
        return model;
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.model.close();
            INSTANCE.model = null;
            INSTANCE = null;
        }
    }

    public void save(String folder) {
        // TODO
    }

    public void load(String folder) {
        // TODO
    }
}
