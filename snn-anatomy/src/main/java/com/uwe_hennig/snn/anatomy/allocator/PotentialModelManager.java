/// @(#)PotentialModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.PotentialModel;
import com.uwe_hennig.snn.anatomy.neuron.PotentialPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// PotentialModelManager
///
/// @author Uwe Hennig
///
public class PotentialModelManager {
    private static final String        FILENAME = "potential.snn";
    private static PotentialModelManager INSTANCE;

    private PotentialModel model;

    private PotentialModelManager(int size) {
        this.model = new PotentialModel(size);
    }

    public static PotentialModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new PotentialModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static PotentialModelManager instance() {
        return INSTANCE;
    }

    public int claimSlot() {
        return model.claimSlot();
    }

    public PotentialModel getModel() {
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
            PotentialPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Potential not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = PotentialPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Potential not loaded!");
        }
    }
}
