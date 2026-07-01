/**
 * @(#)SomaAllocator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.allocator;

import com.uwe_hennig.snn.anatomy.neuron.PlasticityModel;
import com.uwe_hennig.snn.anatomy.neuron.PlasticityView;
import com.uwe_hennig.snn.anatomy.neuron.PotentialModel;
import com.uwe_hennig.snn.anatomy.neuron.PotentialView;
import com.uwe_hennig.snn.anatomy.neuron.SomaModel;
import com.uwe_hennig.snn.anatomy.neuron.SomaView;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdModel;
import com.uwe_hennig.snn.anatomy.neuron.ThresholdView;

/**
 * SomaAllocator
 *
 * @author Uwe Hennig
 */
public class SomaAllocator {
    private static SomaAllocator INSTANCE;

    private final SomaModel model;
    private final PotentialModel potentialModel;
    private final ThresholdModel thresholdModel;
    private final PlasticityModel ltpModel;
    private final PlasticityModel stpModel;

    private int nextOffset = 0;

    private SomaAllocator(int capacity) {
        this.model = new SomaModel(capacity);
        this.potentialModel = new PotentialModel(capacity);
        this.thresholdModel = new ThresholdModel(capacity);
        this.ltpModel = new PlasticityModel(capacity);
        this.stpModel = new PlasticityModel(capacity);
    }

    public static SomaAllocator initInstance(int capacity) {
        INSTANCE = new SomaAllocator(capacity);
        return INSTANCE;
    }

    public static SomaAllocator instance() {
        return INSTANCE;
    }

    public SomaView newSomaView(int fieldId, int neuronId, int axonId) {
        if (nextOffset >= model.getCapacity()) {
            throw new IllegalStateException("Out of Offheap memory");
        }

        int offset = nextOffset++;

        SomaView somaView = createViews(offset);
        somaView.setStructure(fieldId, neuronId, axonId);

        return somaView;
    }

    public SomaView viewAt(int viewId) {
        if (viewId >= nextOffset) {
            return null;
        }
        return createViews(viewId);
    }

    private SomaView createViews(int viewId) {
        if (viewId >= model.getCapacity()) {
            throw new IllegalStateException("Out of Offheap memory");
        }

        PotentialView potentialView = new PotentialView(viewId, potentialModel);
        ThresholdView thresholdView = new ThresholdView(viewId, thresholdModel);
        PlasticityView stpView = new PlasticityView(viewId, stpModel);
        PlasticityView ltpView = new PlasticityView(viewId, ltpModel);

        SomaView somaView = new SomaView(viewId, model, potentialView, thresholdView, stpView, ltpView);
        return somaView;
    }

    public void close() {
        model.close();
        potentialModel.close();
        thresholdModel.close();
        stpModel.close();
        ltpModel.close();
    }
}
