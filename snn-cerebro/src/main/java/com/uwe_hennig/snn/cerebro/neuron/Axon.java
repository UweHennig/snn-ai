/**
 * @(#)Axon.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.allocator.AxonModelManager;
import com.uwe_hennig.snn.anatomy.allocator.BufferedTransferModelManager;
import com.uwe_hennig.snn.anatomy.neuron.AxonModel;
import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.util.BufferedTransferModel;

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

        transfer(model, offset, stimulusType, stimulusValue);
    }

    private static boolean isExternalStimulus(AxonModel model, int offset, int srcNeuronId) {
        int neuronId = AxonView.getNeuronId(model, offset);
        if (neuronId != srcNeuronId) {
            return true;
        }
        return false;
    }

    private static void transfer(AxonModel model, int offset, int stimulusType, float stimulusValue) {
        int blockOffset = AxonView.getBlockId(model, offset);

        BufferedTransferModel transferModel = BufferedTransferModelManager.instance().getModel();
        int entries = transferModel.getEntries(blockOffset);

        for (int i = 0; i < entries; i++) {
            if (StimulusType.isTimeFeedback(stimulusType)) {
                transferModel.offerFbTime(blockOffset, i, stimulusValue);
            }
            if (StimulusType.isValueFeedback(stimulusType)) {
                transferModel.offerFbValue(blockOffset, i, stimulusValue);
            }
            if (StimulusType.isStimulus(stimulusType)) {
                transferModel.offerStimulus(blockOffset, i, stimulusValue);
            }
        }
    }
}
