/// @(#)NeuronFieldModelManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// NeuronFieldModelManager
///
/// @author Uwe Hennig
///
public final class NeuronFieldModelManager {
    private static final String            FILENAME = "field.snn";
    private static NeuronFieldModelManager INSTANCE;

    private NeuronFieldModel model;

    private NeuronFieldModelManager(int size) {
        model = new NeuronFieldModel(size);
    }

    public static NeuronFieldModelManager init(int size) {
        if (INSTANCE == null) {
            synchronized (NeuronFieldModelManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronFieldModelManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static NeuronFieldModelManager instance() {
        return INSTANCE;
    }

    public int claimField() {
        return model.claimField();
    }

    public NeuronFieldModel getModel() {
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
            NeuronFieldPersistence.save(model, path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "NeuronField not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = NeuronFieldPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Dendrit not loaded!");
        }
    }
}
