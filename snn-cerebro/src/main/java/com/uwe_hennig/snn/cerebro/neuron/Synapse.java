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
import com.uwe_hennig.snn.contracts.core.ViewIdentity;
import com.uwe_hennig.snn.services.NeuronElementRegistry;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * Synapse
 *
 * @author Uwe Hennig
 */
public final class Synapse extends ViewIdentity implements NeuronElement {
    private final SynapseView   view;
    private final ModulatorView modulatorView;
    private int targetRef;

    public Synapse(SynapseView view, ModulatorView modulatorView) {
        this.view = view;
        this.modulatorView = modulatorView;
    }

    @Override
    public void stimulate(int stimulusIdentifier) {
        try {
            view.writeLock();
            // TODO complete implementation
            float currentTime = 1000; // TODO

            float stimulusValue = StimulusService.getValue(stimulusIdentifier);
            int stimulusType = StimulusService.getEventType(stimulusIdentifier);

            if (StimulusService.isStimulus(stimulusIdentifier) && isExternalStimulus(stimulusIdentifier)) {
                stimulusValue = modulatorView.applyStimulus(stimulusValue, currentTime);
            } else if (StimulusService.isStimulus(stimulusIdentifier)) {
                stimulusValue = modulatorView.applyStimulus(stimulusType, currentTime);
            }

            stimulusIdentifier = StimulusService.update(stimulusIdentifier, stimulusType, stimulusValue, targetRef);
            SnnTransferservice.transfer(stimulusIdentifier);

        } finally {
            view.writeUnlock();
        }
    }

    public void setTargetRef(int targetRef) {
        this.targetRef = targetRef;
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

    @Override
    public int getViewId() {
        return view.getViewId();
    }
}
