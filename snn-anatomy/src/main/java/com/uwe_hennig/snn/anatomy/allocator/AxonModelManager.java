/// @(#)AxonModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.AxonModel;
import com.uwe_hennig.snn.anatomy.neuron.AxonPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// AxonModelManager
///
/// @author Uwe Hennig
///
public class AxonModelManager {
    private static final String     FILENAME = "axon.snn";
    private static AxonModelManager INSTANCE;

    private AxonModel model;

    private AxonModelManager(int size) {
        this.model = new AxonModel(size);
    }

    public static AxonModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (AxonModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AxonModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static AxonModelManager instance() {
        return INSTANCE;
    }

    public int claimSlot(int fieldOffset, int neuronOffset, int targetOffset) {
        return model.claimSlot(fieldOffset, neuronOffset, targetOffset);
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
            AxonPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Axon not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = AxonPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Axon not loaded!");
        }
    }
}
