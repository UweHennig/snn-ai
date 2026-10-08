/**
 * @(#)Synapse.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.allocator.SynapseModelManager;
import com.uwe_hennig.snn.anatomy.neuron.SynapseModel;
import com.uwe_hennig.snn.anatomy.neuron.SynapseView;

/**
 * Synapse
 *
 * @author Uwe Hennig
 */
public final class Synapse {

    private Synapse() {
    }

    public void stimulate(int offset, int srcNeuronId, int srcType, int stimulusType, float stimulusValue) {
        SynapseModel model = SynapseModelManager.instance().getModel();
        SynapseView.writeLock(model, offset);

        try {
            //            // TODO complete implementation
            //            float currentTime = 1000; // TODO
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
            //            stimulusIdentifier = StimulusService.update(stimulusIdentifier, stimulusType, stimulusValue, targetId);

        } finally {
            SynapseView.writeUnlock(model, offset);
        }

        // TODO transfer!
    }
}
