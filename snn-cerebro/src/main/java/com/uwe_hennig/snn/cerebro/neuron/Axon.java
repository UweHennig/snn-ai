/**
 * @(#)Axon.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.allocator.AxonModelManager;
import com.uwe_hennig.snn.anatomy.neuron.AxonModel;
import com.uwe_hennig.snn.anatomy.neuron.AxonView;

/**
 * Axon
 *
 * @author Uwe Hennig
 */
public final class Axon {
    private Axon() {
    }

    public static void stimulate(int offset, int srcNeuronId, int srcType, int stimulusType, float stimulusValue) {
        if (Math.abs(stimulusValue) < 0.01) {
            return;
        }

        float currentTime = 1000; // TODO
        AxonModel model = AxonModelManager.instance().getModel();

        AxonView.writeLock(model, offset);
        try {
            // TODO complete implementation

            if (isExternalStimulus(model, offset, srcNeuronId)) {
                //stimulusValue = ModulatorView.applyModulation(modulatorViewId, stimulusValue, currentTime);
            } else {
                // TODO Verfahren auch für Feedbacks definieren!
                //stimulusValue = ModulatorView.applyStimulus(modulatorViewId, stimulusValue, currentTime);
            }

        } finally {
            AxonView.writeUnlock(model, offset);
        }

        // TODO Axon must terminate thread activity
        // TODO No transfer, but new values in the BufferedTransferSegment
    }

    private static boolean isExternalStimulus(AxonModel model, int offset, int srcNeuronId) {
        int neuronId = AxonView.getNeuronId(model, offset);
        if (neuronId != srcNeuronId) {
            return true;
        }
        return false;
    }
}
