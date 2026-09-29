/// @(#)DendritAllocator.java
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
public class DendritModelManager {
    private static final String        FILENAME = "dendrit.snn";
    private static DendritModelManager INSTANCE;

    private DendritModel model;

    private DendritModelManager(int capacity) {
        model = new DendritModel(capacity);
    }

    public static DendritModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new DendritModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static DendritModelManager instance() {
        return INSTANCE;
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
