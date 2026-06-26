/**
 * @(#)AgentMediator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.afferent.agent;

import java.util.HashMap;
import java.util.Map;

import com.uwe_hennig.snn.contracts.afferent.ActionChannel;
import com.uwe_hennig.snn.contracts.afferent.Converter;
import com.uwe_hennig.snn.contracts.afferent.EnvAction;
import com.uwe_hennig.snn.contracts.afferent.EnvFeedback;
import com.uwe_hennig.snn.contracts.afferent.EnvSignal;
import com.uwe_hennig.snn.contracts.afferent.EnvState;
import com.uwe_hennig.snn.contracts.afferent.FeedbackChannel;
import com.uwe_hennig.snn.contracts.afferent.SnnEffector;
import com.uwe_hennig.snn.contracts.afferent.SnnFeedback;
import com.uwe_hennig.snn.contracts.afferent.SnnReceptor;
import com.uwe_hennig.snn.contracts.afferent.StateChannel;

/**
 * AgentMediator
 *
 * <pre>
 * The AgentMediator is responsible for organizing communication between the environment and the SNN.
 *
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
    protected final Map<Long, StateChannel>    stateChhannels   = new HashMap<>();
    protected final Map<Long, ActionChannel>   actionChannels   = new HashMap<>();
    protected final Map<Long, FeedbackChannel> feedbackChannels = new HashMap<>();

    // --- (EnvState, EnvSignal) → (SnnReceptor, Float) ---

    public void registerState(EnvState<?> state, Converter<EnvSignal<?>, Float> converter, SnnReceptor receptor) {
        StateChannel stateChannel = new StateChannel(state, converter, receptor);
        stateChhannels.put(state.getIdentifier(), stateChannel);
        state.setConsumer(this::onState);
    }

    protected void onState(EnvState<?> state, EnvSignal<?> signal) {
        StateChannel stateChannel = stateChhannels.get(state.getIdentifier());
        Converter<EnvSignal<?>, Float> converter = stateChannel.getConverter();
        SnnReceptor receptor = stateChannel.getReceptor();

        if (converter != null && receptor != null) {
            float convertedValue = converter.convert(signal);
            receptor.perceive(convertedValue);
        }
    }

    // --- (EnvFeedback, EnvSignal) → (SnnFeedback, Float) ---

    public void registerEnvFeedback(EnvFeedback<?> envFeedback, Converter<EnvSignal<?>, Float> converter, SnnFeedback snnFeedback) {
        FeedbackChannel feedbackChannel = new FeedbackChannel(envFeedback, converter, snnFeedback);
        feedbackChannels.put(envFeedback.getIdentifier(), feedbackChannel);
        envFeedback.setConsumer(this::onFeedback);
    }

    protected void onFeedback(EnvFeedback<?> envFeedback, EnvSignal<?> signal) {
        FeedbackChannel feedbackChannel = feedbackChannels.get(envFeedback.getIdentifier());
        Converter<EnvSignal<?>, Float> converter = feedbackChannel.getConverter();
        SnnFeedback snnFeedback = feedbackChannel.getSnnFeedback();

        if (converter != null && snnFeedback != null) {
            float value = converter.convert(signal);
            snnFeedback.perceive(value);
        }
    }

    // --- (SnnEffector, Float) → (EnvAction, EnvSignal) ---

    public void registerEffector(SnnEffector effector, Converter<Float, EnvSignal<?>> transformer, EnvAction<?> envAction) {
        ActionChannel actionChannel = new ActionChannel(effector, transformer, envAction);
        actionChannels.put(effector.getIdentifier(), actionChannel);
        effector.setConsumer(this::onEffector);
    }

    protected void onEffector(SnnEffector effector, float value) {
        ActionChannel actionChannel = actionChannels.get(effector.getIdentifier());
        Converter<Float, EnvSignal<?>> converter = actionChannel.getConverter();
        EnvAction<?> envAction = actionChannel.getAction();

        if (converter != null && envAction != null) {
            EnvSignal<?> signal = converter.convert(value);
            envAction.invoke(signal);
        }
    }
}
