/// @(#)NeuronFieldManager.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.allocator;

import java.io.IOException;
import java.nio.file.Path;

import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;
import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldPersistence;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// NeuronFieldManager
///
/// @author Uwe Hennig
///
public class NeuronFieldManager {
    private static final String       FILENAME = "field.snn";
    private static NeuronFieldManager INSTANCE;

    private NeuronFieldModel model;

    private NeuronFieldManager(int size) {
        model = new NeuronFieldModel(size);
    }

    public static NeuronFieldManager init(int size) {
        if (INSTANCE == null) {
            synchronized (NeuronFieldManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronFieldManager(size);
                }
            }
        }
        return INSTANCE;
    }

    public static NeuronFieldManager instance() {
        return INSTANCE;
    }

    public int claimSlot() {
        return model.claimSlot();
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
            SNNLogger.error(() -> "Field not saved!");
        }
    }

    public void load(String folder) {
        try {
            Path path = Path.of(folder).resolve(FILENAME);
            this.model = NeuronFieldPersistence.load(path);
        } catch (IOException e) {
            e.printStackTrace();
            SNNLogger.error(() -> "Field not loaded!");
        }
    }
}
