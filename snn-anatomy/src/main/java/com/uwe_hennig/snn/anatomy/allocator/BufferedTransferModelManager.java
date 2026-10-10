/// @(#)BufferedTransferModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.nio.file.Path;

import com.uwe_hennig.snn.util.BufferedTransferModel;

/// BufferedTransferModelManager
///
/// @author Uwe Hennig
///
public class BufferedTransferModelManager {
    private static final String                 FILENAME = "trasfer.snn";
    private static BufferedTransferModelManager INSTANCE;

    private BufferedTransferModel model;

    private BufferedTransferModelManager(int size) {
        this.model = new BufferedTransferModel(size);
    }

    public static BufferedTransferModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (BufferedTransferModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BufferedTransferModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static BufferedTransferModelManager instance() {
        return INSTANCE;
    }

    // claimSlot(10, srcNeuronId, axonId, AXON) creates space for 10 Synapses for an axon
    public int claimSlot(int numNeuronelements, int parentId, int srcId, int srcType) {
        return model.claimSlot(numNeuronelements, parentId, srcId, srcType);
    }

    public BufferedTransferModel getModel() {
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
        Path path = Path.of(folder).resolve(FILENAME);
        // TODO
        // BufferedTransferModel.save(this.model, path);
    }

    public void load(String folder) {
        Path path = Path.of(folder).resolve(FILENAME);
        // TODO
        //this.model = BufferedTransferModel.load(path);
    }
}
