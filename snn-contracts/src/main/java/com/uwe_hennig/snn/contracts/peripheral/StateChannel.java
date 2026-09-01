/**
 * @(#)StateChannel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.contracts.peripheral;

/**
 * StateChannel
 * links the environment state to the Snn receptor
 *
 * @author Uwe Hennig
 */
public class StateChannel {
    private final EnvState state;
    private final SnnReceptor receptor;

    private final Converter<EnvSignal<?>, float [][]> converter;

    public StateChannel(EnvState state, Converter<EnvSignal<?>, float [][]> converter, SnnReceptor receptor) {
        this.state = state;
        this.converter = converter;
        this.receptor = receptor;
    }

    public EnvState getState() {
        return state;
    }

    public Converter<EnvSignal<?>, float [][]> getConverter() {
        return converter;
    }

    public SnnReceptor getReceptor() {
        return receptor;
    }

    // source identifier is associated with the channel
    public long getIdentifier() {
        return state.getIdentifier();
    }
}
