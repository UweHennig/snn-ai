/// @(#)PlasticityModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.PlasticityModel;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// PlasticityModelManager
///
/// @author Uwe Hennig
///
public class PlasticityModelManager {
    private static final String           FILENAME = "plasticity.snn";
    private static PlasticityModelManager INSTANCE;

    private PlasticityModel model;

    private PlasticityModelManager(int size) {
        this.model = new PlasticityModel(size);
    }

    public static PlasticityModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PlasticityModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static PlasticityModelManager instance() {
        return INSTANCE;
    }

    public int claimSlot() {
        return model.claimSlot();
    }

    public PlasticityModel getModel() {
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
            PlasticityPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Plasticity not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = PlasticityPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Plasticity not loaded!");
        }
    }
}
