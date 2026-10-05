/// @(#)ThresholdModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.ThresholdModel;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// ThresholdModelManager
///
/// @author Uwe Hennig
///
public class ThresholdModelManager {
    private static final String          FILENAME = "threshold.snn";
    private static ThresholdModelManager INSTANCE;

    private ThresholdModel model;

    private ThresholdModelManager(int capacity) {
        this.model = new ThresholdModel(capacity);
    }

    public static ThresholdModelManager init(int capacity) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ThresholdModelManager(capacity);
                }
            }
        }
        return INSTANCE;
    }

    public static ThresholdModelManager instance() {
        return INSTANCE;
    }

    public ThresholdModel getModel() {
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
            ThresholdPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Threshold not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = ThresholdPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Threshold not loaded!");
        }
    }
}
