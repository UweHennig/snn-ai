/**
 * @(#)Axon.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.cerebro.neuron;

import static com.uwe_hennig.snn.contracts.core.NeuronElementType.AXON;

import com.uwe_hennig.snn.anatomy.neuron.AxonView;
import com.uwe_hennig.snn.anatomy.neuron.ModulatorView;
import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;
import com.uwe_hennig.snn.contracts.core.ViewIdentity;
import com.uwe_hennig.snn.services.NeuronElementRegistry;

/**
 * Axon
 *
 * @author Uwe Hennig
 */
public final class Axon extends ViewIdentity implements NeuronElement {
    private final AxonView      axonView;
    private final ModulatorView modulatorView;

    public Axon(AxonView axonView, ModulatorView modulatorView) {
        this.axonView = axonView;
        this.modulatorView = modulatorView;
    }

    @Override
    public void stimulate(int stimulusIdentifier) {
        try {
            axonView.writeLock();
            // TODO Axon must terminate thread activit and create a new Matrix Event
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
            //            // TODO for each synapse or bulk
            //            stimulusIdentifier = StimulusService.update(stimulusIdentifier, stimulusType, stimulusValue, synapseRef);
            //            SnnTransferservice.transfer(stimulusIdentifier);
        } finally {
            axonView.writeUnlock();
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
        return axonView.getNeuronId();
    }

    @Override
    public int getViewId() {
        return axonView.getId();
    }
}
