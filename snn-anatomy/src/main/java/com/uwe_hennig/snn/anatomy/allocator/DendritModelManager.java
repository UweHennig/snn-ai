/// @(#)DendritModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.DendritModel;
import com.uwe_hennig.snn.anatomy.neuron.DendritPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// DendritModelManager
///
/// @author Uwe Hennig
///
public class DendritModelManager {
    private static final String        FILENAME = "dendrit.snn";
    private static DendritModelManager INSTANCE;

    private DendritModel model;

    private DendritModelManager(int size) {
        model = new DendritModel(size);
    }

    public static DendritModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DendritModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static DendritModelManager instance() {
        return INSTANCE;
    }

    public int claimSlot(int fieldOffset, int neuronOffset, int targetOffset) {
        return model.claimSlot(fieldOffset, neuronOffset, targetOffset);
    }

    public DendritModel getModel() {
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
            DendritPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Dendrit not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = DendritPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Dendrit not loaded!");
        }
    }
}
