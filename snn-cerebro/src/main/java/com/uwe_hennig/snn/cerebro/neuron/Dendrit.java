/// @(#)Dendrit.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.allocator.DendritModelManager;
import com.uwe_hennig.snn.anatomy.allocator.WeightModelManager;
import com.uwe_hennig.snn.anatomy.neuron.DendritModel;
import com.uwe_hennig.snn.anatomy.neuron.DendritView;
import com.uwe_hennig.snn.anatomy.neuron.WeightModel;
import com.uwe_hennig.snn.anatomy.neuron.WeightView;
import com.uwe_hennig.snn.contracts.core.StimulusType;

/// Dendrit
///
/// @author Uwe Hennig
public final class Dendrit {
    private Dendrit() {
    }

    /// General form of the stimulus method
    ///
    /// @param offset
    ///            memory address of the dendrite segment
    /// @param srcId
    ///            is the source identifier
    /// @param srcType
    ///            is the type of the source
    /// @param stimulusType
    ///            is the stimulus type: FEEDBACK_TIME, FEEDBACK_VALUE, STIMULUS
    /// @param stimulusValue
    ///            a float delta or absolute value depending on stimulusType
    public static void stimulate(int offset, int srcId, int srcType, int stimulusType, float stimulusValue) {
        DendritModel model = DendritModelManager.instance().getModel();

        try {
            final float currentTime = 1000; // TODO Model time

            DendritView.writeLock(model, offset);

            if (StimulusType.isStimulus(stimulusType)) {
                int weightId = DendritView.getWeightId(model, offset);
                WeightModel weightModel = WeightModelManager.instance().getModel();
                WeightView.applyStimulus(weightModel, weightId, stimulusValue, currentTime);
            }

            if (StimulusType.isTimeFeedback(stimulusType)) {
                int weightId = DendritView.getWeightId(model, offset);
                WeightModel weightModel = WeightModelManager.instance().getModel();
                WeightView.applyFeedback(weightModel, weightId, stimulusValue);
            }

            int somaId = DendritView.getSomaId(model, offset);
            // TODO forward values to soma

        } finally {
            DendritView.writeUnlock(model, offset);
        }
    }
}
