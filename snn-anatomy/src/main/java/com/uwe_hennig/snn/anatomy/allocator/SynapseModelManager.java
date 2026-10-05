/// @(#)SynapseModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.SynapseModel;
import com.uwe_hennig.snn.anatomy.neuron.SynapsePersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// SynapseModelManager
///
/// @author Uwe Hennig
///
public class SynapseModelManager {
    private static final String        FILENAME = "synapse.snn";
    private static SynapseModelManager INSTANCE;

    private SynapseModel model;

    private SynapseModelManager(int size) {
        model = new SynapseModel(size);
    }

    public static SynapseModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (SynapseModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SynapseModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static SynapseModelManager instance() {
        return INSTANCE;
    }

    public SynapseModel getModel() {
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
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            SynapsePersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Synapse not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = SynapsePersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Synapse not loaded!");
        }
    }
}
