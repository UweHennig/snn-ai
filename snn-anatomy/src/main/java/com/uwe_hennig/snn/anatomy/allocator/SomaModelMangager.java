/// @(#)SomaModelMangager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.SomaModel;
import com.uwe_hennig.snn.anatomy.neuron.SomaPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// SomaModelMangager
///
/// @author Uwe Hennig
/// 
public class SomaModelMangager {
    private static final String      FILENAME = "soma.snn";
    private static SomaModelMangager INSTANCE;

    private SomaModel model;

    private SomaModelMangager(int size) {
        this.model = new SomaModel(size);
    }

    public static SomaModelMangager init(int size) {
        if (INSTANCE == null) {
            synchronized (PlasticityModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SomaModelMangager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static SomaModelMangager instance() {
        return INSTANCE;
    }

    public SomaModel getModel() {
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
            SomaPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Soma not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = SomaPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Soma not loaded!");
        }
    }
}
