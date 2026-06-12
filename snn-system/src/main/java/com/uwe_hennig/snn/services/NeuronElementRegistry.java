/**
 * @(#)NeuronElementRegistry.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.services;

import com.uwe_hennig.snn.contracts.core.NeuronElement;
import com.uwe_hennig.snn.contracts.core.NeuronElementType;

/**
 * NeuronElementRegistry
 *
 * @author Uwe Hennig
 */
public final class NeuronElementRegistry {
    private final NeuronElement[] dendritArray;
    private final NeuronElement[] somaArray;
    private final NeuronElement[] axonArray;
    private final NeuronElement[] synapseArray;

    private static NeuronElementRegistry INSTANCE;

    public static NeuronElementRegistry of(int dendritSize, int somaSize, int axonSize, int synapseSize) {
        if (INSTANCE == null) {
            synchronized (NeuronElementRegistry.class) {
                if (INSTANCE == null) {
                    INSTANCE = new NeuronElementRegistry(dendritSize, somaSize, axonSize, synapseSize);
                }
            }
        }
        return INSTANCE;
    }

    private NeuronElementRegistry(int dendritSize, int somaSize, int axonSize, int synapseSize) {
        this.dendritArray = new NeuronElement[dendritSize];
        this.somaArray = new NeuronElement[somaSize];
        this.axonArray = new NeuronElement[axonSize];
        this.synapseArray = new NeuronElement[synapseSize];
    }

    public static NeuronElementRegistry instance() {
        return INSTANCE;
    }

    public void registerNeuronElement(int idx, NeuronElement element) {
        switch (element.getType()) {
            case DENDRIT:
                dendritArray[idx] = element;
                break;
            case SOMA:
                somaArray[idx] = element;
                break;
            case AXON:
                axonArray[idx] = element;
                break;
            case SYNAPSE:
                synapseArray[idx] = element;
                break;
        }
    }

    public NeuronElement getNeuronElement(int idx, NeuronElementType type) {
        return switch (type) {
            case DENDRIT -> dendritArray[idx];
            case SOMA -> somaArray[idx];
            case AXON -> axonArray[idx];
            case SYNAPSE -> synapseArray[idx];
        };
    }
}
