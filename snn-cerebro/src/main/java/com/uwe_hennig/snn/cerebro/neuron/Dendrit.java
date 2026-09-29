/// @(#)Dendrit.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.allocator.DendritModelManager;
import com.uwe_hennig.snn.anatomy.neuron.DendritModel;
import com.uwe_hennig.snn.anatomy.neuron.DendritView;

/// Dendrit
///
/// @author Uwe Hennig
public final class Dendrit {
    private Dendrit() {
    }

    /// General form of the stimulus method
    ///
    /// @param offset memory address of the dendrite segment
    /// @param srcId is the source identifier
    /// @param srcType is the type of the source
    /// @param stimulusType is the stimulus type: FEEDBACK_TIME, FEEDBACK_VALUE, STIMULUS
    /// @param stimulusValue a float delta or absolute value depending on stimulusType
    public static void stimulate(int offset, int srcId, int srcType, int stimulusType, float stimulusValue) {
        DendritModel model = DendritModelManager.instance().getModel();

        try {
            DendritView.writeLock(model, offset);
// TODO
//            float currentTime = 1000; // TODO Model time
//
//            float stimulusValue = StimulusService.getValue(stimulusIdentifier);
//            if (StimulusService.isStimulus(stimulusIdentifier)) {
//                stimulusValue = WeightView.applyStimulus(weightId, stimulusValue, currentTime);
//                stimulusIdentifier = StimulusService.update(stimulusIdentifier, StimulusType.STIMULUS.code(), stimulusValue, DendritView.getSomaId(viewId));
//            }
//            if (StimulusService.isTimeFeedback(stimulusIdentifier)) {
//                WeightView.applyFeedback(weightId, stimulusValue);
//            }
//
//            SnnTransferservice.transfer(stimulusIdentifier);

        } finally {
            DendritView.writeUnlock(model, offset);
        }
    }
}
