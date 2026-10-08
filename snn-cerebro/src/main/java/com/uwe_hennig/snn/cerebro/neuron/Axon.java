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
        AxonModel model = AxonModelManager.instance().getModel();
        AxonView.writeLock(model, offset);

        //            float currentTime = 1000; // TODO
        try {
            // TODO Axon must terminate thread activity
            //            // TODO complete implementation
            //
            //            float stimulusValue = StimulusService.getValue(stimulusIdentifier);
            //            int stimulusType = StimulusService.getStimulusType(stimulusIdentifier);
            //
            //            if (StimulusService.isStimulus(stimulusIdentifier) && isExternalStimulus(stimulusIdentifier)) {
            //                stimulusValue = ModulatorView.applyStimulus(modulatorViewId, stimulusValue, currentTime);
            //            } else if (StimulusService.isStimulus(stimulusIdentifier)) {
            //                stimulusValue = ModulatorView.applyStimulus(modulatorViewId, stimulusType, currentTime);
            //            }
            //
            //            // TODO for each synapse or bulk
            //            stimulusIdentifier = StimulusService.update(stimulusIdentifier, stimulusType, stimulusValue, synapseRef);
        } finally {
            AxonView.writeUnlock(model, offset);
        }

        // TODO kein Transfer, aber neue Werte im BufferedTransferSegment
    }
}
