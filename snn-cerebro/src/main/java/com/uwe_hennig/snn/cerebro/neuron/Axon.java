/**
 * @(#)Axon.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static com.uwe_hennig.snn.contracts.core.NeuronElementType.AXON;
import static com.uwe_hennig.snn.contracts.core.NeuronElementType.SYNAPSE;

import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;
import com.uwe_hennig.snn.contracts.core.ViewIdentity;
import com.uwe_hennig.snn.services.NeuronElementRegistry;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * Axon
 *
 * @author Uwe Hennig
 */
public final class Axon  extends ViewIdentity implements NeuronElement {
    private final AxonView      view;
    private final ModulatorView modulatorView;
    private final int synapseRef;

    // TODO remove ModulatorView modulatorView - see AxonView view
    public Axon(AxonView view, ModulatorView modulatorView, int synapseRef) {
        this.view = view;
        this.modulatorView = modulatorView;
        this.synapseRef = synapseRef;
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

            // TODO for each synapse or bulk
            stimulusIdentifier = StimulusService.update(stimulusIdentifier, stimulusType, stimulusValue, synapseRef);
            SnnTransferservice.transfer(stimulusIdentifier);
        } finally {
            view.writeUnlock();
        }
    }

    @Override
    public NeuronElementType getType() {
        return AXON;
    }

    private boolean isExternalStimulus(int stimulusIdentifier) {
        NeuronElement neuronElement = NeuronElementRegistry.instance().getNeuronElement(stimulusIdentifier, AXON);
        return neuronElement != null && neuronElement.getNeuronId() != this.getNeuronId();
    }

    @Override
    public int getNeuronId() {
        return view.getNeuronId();
    }

    @Override
    public int getViewId() {
        return view.getViewId();
    }
}
