/**
 * @(#)AgentMediator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.afferent.agent;

import java.util.HashMap;
import java.util.Map;

import com.uwe_hennig.snn.contracts.afferent.Converter;
import com.uwe_hennig.snn.contracts.afferent.EnvState;
import com.uwe_hennig.snn.contracts.afferent.Signal;
import com.uwe_hennig.snn.contracts.afferent.SnnReceptor;
import com.uwe_hennig.snn.contracts.afferent.StateChannel;

/**
 * AgentMediator The AgentMediator is responsible for organizing communication between the Environment and the SNN.
 *
 * <pre>
 * Environment  <-> SNN
 * --------------------------
 * EnvState     ->  Receptor
 * EnvFeedback  ->  Feedback
 * EnvAction    <-  Effector
 * </pre>
 *
 * @author Uwe Hennig
 */
public abstract class AgentMediator {
    protected final Map<Long, StateChannel> stateChhannels = new HashMap<>();

    public void registerState(EnvState<?> state, Converter<Signal<?>, Float> transformer, SnnReceptor receptor) {
        StateChannel stateChannel = new StateChannel(state, transformer, receptor);
        stateChhannels.put(state.getIdentifier(), stateChannel);
        state.setConsumer(this::onState);
    }

    // Dispatcher: (EnvState,Payload) → (Receptor, float)
    protected void onState(EnvState<?> state, Signal<?> payload) {
        StateChannel stateChannel = stateChhannels.get(state.getIdentifier());
        Converter<Signal<?>, Float> converter = stateChannel.getConverter();
        SnnReceptor receptor = stateChannel.getReceptor();

        if (converter != null && receptor != null) {
            float transformedValue = converter.convert(payload);
            receptor.perceive(transformedValue);
        }
    }

}
