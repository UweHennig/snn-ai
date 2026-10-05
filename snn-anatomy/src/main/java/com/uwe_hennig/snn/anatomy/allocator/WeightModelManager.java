/**
 * @(#)WeightModelManager.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.WeightModel;
import com.uwe_hennig.snn.anatomy.neuron.WeightPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// WeightModelManager
///
/// @author Uwe Hennig
///
public class WeightModelManager {
    private static final String       FILENAME = "weight.snn";
    private static WeightModelManager INSTANCE;

    private WeightModel model;

    private WeightModelManager(int size) {
        this.model = new WeightModel(size);
    }

    public static WeightModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new WeightModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static WeightModelManager instance() {
        return INSTANCE;
    }

    public WeightModel getModel() {
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
            WeightPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Weight not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = WeightPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Weight not loaded!");
        }
    }
}