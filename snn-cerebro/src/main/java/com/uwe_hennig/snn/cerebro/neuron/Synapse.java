/// @(#)Synapse.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.neuron;

import com.uwe_hennig.snn.anatomy.allocator.ModulatorModelManager;
import com.uwe_hennig.snn.anatomy.allocator.SynapseModelManager;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorModel;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.SynapseModel;
import com.uwe_hennig.snn.anatomy.neuron.SynapseView;
import com.uwe_hennig.snn.contracts.core.ElementType;
import com.uwe_hennig.snn.contracts.core.StimulusType;

/// Synapse
///
/// @author Uwe Hennig
///
public final class Synapse {
    private Synapse() {
    }

    public static void stimulate(int offset, int srcNeuronId, int srcType, int trgType, int stimulusType, float stimulusValue) {
        if (Math.abs(stimulusValue) < 0.01) {
            return;
        }

        SynapseModel model = SynapseModelManager.instance().getModel();

        float currentTime = 1000; // TODO
        float adjustedValue = stimulusValue;

        SynapseView.writeLock(model, offset);
        try {
            if (StimulusType.isStimulus(stimulusType) && isExternalStimulus(model, offset, srcNeuronId)) {
                ModulatorModel modulatorModel = ModulatorModelManager.instance().getModel();
                int modulatorId = SynapseView.getModulatorId(model, offset);

                ModulatorView.applyModulation(modulatorModel, modulatorId, stimulusValue, currentTime, stimulusValue < 0);
            } else if (StimulusType.isStimulus(stimulusType)) {
                ModulatorModel modulatorModel = ModulatorModelManager.instance().getModel();
                int modulatorId = SynapseView.getModulatorId(model, offset);

                adjustedValue = ModulatorView.applyStimulus(modulatorModel, modulatorId, stimulusValue, currentTime);
            }
        } finally {
            SynapseView.writeUnlock(model, offset);
        }

        int trgId = SynapseView.getTargetId(model, offset);
        transfer(stimulusType, srcNeuronId, ElementType.SYNAPSE, trgId, trgType, adjustedValue);
    }

    private static void transfer(int stimulusType, int srcNeuronId, int srcType, int trgId, int trgType, float stimulusValue) {
        if (ElementType.isDendrit(trgType)) {
            Dendrit.stimulate(trgId, srcNeuronId, srcType, stimulusType, stimulusValue);
        }

        if (ElementType.isSoma(trgType)) {
            Soma.stimulate(trgId, srcNeuronId, srcType, stimulusType, stimulusValue);
        }

        if (ElementType.isAxon(trgType)) {
            Axon.stimulate(trgId, srcNeuronId, srcType, stimulusType, stimulusValue);
        }

        if (ElementType.isSynapse(trgType)) {
            Synapse.stimulate(trgId, srcNeuronId, srcType, trgType, stimulusType, stimulusValue);
        }

        // TODO ElementType.isEffector(trgType)
    }

    private static boolean isExternalStimulus(SynapseModel model, int offset, int srcNeuronId) {
        int neuronId = SynapseView.getNeuronId(model, offset);
        if (neuronId != srcNeuronId) {
            return true;
        }
        return false;
    }
}
