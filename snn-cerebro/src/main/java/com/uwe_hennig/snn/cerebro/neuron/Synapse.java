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
    private final int viewId;
    private final int modulatorViewId;
    private int       targetId;

    public Synapse(int viewId, int modulatorViewId) {
        this.viewId = viewId;
        this.modulatorViewId = modulatorViewId;
    }

    @Override
    public void stimulate(int stimulusIdentifier) {
        try {
            SynapseView.writeLock(viewId);
            // TODO complete implementation
            float currentTime = 1000; // TODO

            float stimulusValue = StimulusService.getValue(stimulusIdentifier);
            int stimulusType = StimulusService.getEventType(stimulusIdentifier);

            if (StimulusService.isStimulus(stimulusIdentifier) && isExternalStimulus(stimulusIdentifier)) {
                stimulusValue = ModulatorView.applyStimulus(modulatorViewId, stimulusValue, currentTime);
            } else if (StimulusService.isStimulus(stimulusIdentifier)) {
                stimulusValue = ModulatorView.applyStimulus(modulatorViewId, stimulusType, currentTime);
            }

            stimulusIdentifier = StimulusService.update(stimulusIdentifier, stimulusType, stimulusValue, targetId);
            SnnTransferservice.transfer(stimulusIdentifier);

        } finally {
            SynapseView.writeUnlock(viewId);
        }
    }

    public void setTarget(int targetId, int type) {
        this.targetId = targetId;
    }

    public int getTargetId() {
        return this.targetId;
    }

    @Override
    public NeuronElementType getType() {
        return NeuronElementType.SYNAPSE;
    }

    @Override
    public int getNeuronId() {
        return SynapseView.getNeuronId(viewId);
    }

    private boolean isExternalStimulus(int stimulusIdentifier) {
        NeuronElement neuronElement = NeuronElementRegistry.instance().getNeuronElement(stimulusIdentifier, AXON);
        return neuronElement != null && neuronElement.getNeuronId() != this.getNeuronId();
    }

    @Override
    public int getViewId() {
        return viewId;
    }
}
