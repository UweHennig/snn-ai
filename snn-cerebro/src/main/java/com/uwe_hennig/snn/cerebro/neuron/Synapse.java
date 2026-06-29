/**
 * @(#)Synapse.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static com.uwe_hennig.snn.contracts.core.NeuronElementType.AXON;

import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.anatomy.neuron.SynapseView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;
import com.uwe_hennig.snn.services.NeuronElementRegistry;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * Synapse
 *
 * @author Uwe Hennig
 */
public final class Synapse implements NeuronElement {
    private final SynapseView   view;
    private final ModulatorView modulatorView;

    public Synapse(SynapseView view, ModulatorView modulatorView) {
        this.view = view;
        this.modulatorView = modulatorView;
    }

    @Override
    public void stimulate(int stimulusIdentifier) {
        // TODO complete implementation
        float currentTime = 1000; // TODO

        float stimulusValue = StimulusService.getValue(stimulusIdentifier);
        int stimulusType = StimulusService.getTrgType(stimulusIdentifier);

        if (StimulusService.isStimulus(stimulusIdentifier) && isExternalStimulus(stimulusIdentifier)) {
            stimulusValue = modulatorView.applyStimulus(stimulusValue, currentTime);
        } else if (StimulusService.isStimulus(stimulusIdentifier)) {
            stimulusValue = modulatorView.applyStimulus(stimulusType, currentTime);
        }

        StimulusService.update(stimulusIdentifier, stimulusType, view.getViewId(), view.getTargetId(), -1, view.getTargetType(), stimulusValue);
        SnnTransferservice.transfer(stimulusIdentifier, view.getTargetType());
    }

    @Override
    public NeuronElementType getType() {
        return NeuronElementType.SYNAPSE;
    }

    @Override
    public int getNeuronId() {
        return view.getNeuronId();
    }

    private boolean isExternalStimulus(int stimulusIdentifier) {
        NeuronElement neuronElement = NeuronElementRegistry.instance().getNeuronElement(stimulusIdentifier, AXON);
        return neuronElement != null && neuronElement.getNeuronId() != this.getNeuronId();
    }
}
