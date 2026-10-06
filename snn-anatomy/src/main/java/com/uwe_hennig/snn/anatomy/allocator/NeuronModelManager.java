/// @(#)NeuronModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.NeuronModel;
import com.uwe_hennig.snn.anatomy.neuron.NeuronPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// NeuronModelManager
///
/// @author Uwe Hennig
///
public class NeuronModelManager {
    private static final String      FILENAME = "neuron.snn";
    public static NeuronModelManager INSTANCE;

    private NeuronModel model;

    private NeuronModelManager(int size) {
        this.model = new NeuronModel(size);
    }

    public static NeuronModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static NeuronModelManager instance() {
        return INSTANCE;
    }

    public int claimSlot() {
        return model.claimSlot();
    }

    public static void close() {
        if (INSTANCE != null) {
            INSTANCE.model.close();
            INSTANCE.model = null;
            INSTANCE = null;
        }
    }

    public void save(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            NeuronPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Neuron not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = NeuronPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Neuron not loaded!");
        }
    }
}
