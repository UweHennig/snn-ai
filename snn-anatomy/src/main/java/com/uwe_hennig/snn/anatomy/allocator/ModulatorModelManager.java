/// @(#)ModulatorModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.ModulatorModel;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// ModulatorModelManager
///
/// @author Uwe Hennig
////
public class ModulatorModelManager {
    private static final String          FILENAME = "modulator.snn";
    private static ModulatorModelManager INSTANCE;

    private ModulatorModel model;

    private ModulatorModelManager(int size) {
        this.model = new ModulatorModel(size);
    }

    public static ModulatorModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ModulatorModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static ModulatorModelManager instance() {
        return INSTANCE;
    }

    public int claimSlot() {
        return model.claimSlot();
    }

    public ModulatorModel getModel() {
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
            ModulatorPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Modulator not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = ModulatorPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Modulator not loaded!");
        }
    }
}
