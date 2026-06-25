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
import com.uwe_hennig.snn.services.NeuronElementRegistry;
import com.uwe_hennig.snn.services.StimulusService;
import com.uwe_hennig.snn.util.SnnTransferservice;

/**
 * Axon
 *
 * @author Uwe Hennig
 */
public final class Axon implements NeuronElement {
    private final AxonView      view;
    private final ModulatorView modulatorView;

    public Axon(AxonView view, ModulatorView modulatorView) {
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
            // TODO inhibitory/excitatory
        }


        // TODO for each synapse or bulk
        StimulusService.update(stimulusIdentifier, stimulusType, view.getViewId(), -1, view.getSynapseRef(), SYNAPSE.code(), stimulusValue);
        SnnTransferservice.transfer(stimulusIdentifier, SYNAPSE.code());
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
}
