/**
 * @(#)AgentMediator.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.afferent.agent;

import java.util.HashMap;
import java.util.Map;

import com.uwe_hennig.snn.contracts.afferent.EffectorChannel;
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
    protected final Map<Long, StateChannel>    stateChannels    = new HashMap<>();
    protected final Map<Long, EffectorChannel> effectorChannels = new HashMap<>();
    protected final Map<Long, FeedbackChannel> feedbackChannels = new HashMap<>();

    // --- (EnvState, EnvSignal) → (SnnReceptor, Float) ---

    public void registerState(EnvState state, Converter<EnvSignal<?>, Float> converter, SnnReceptor receptor) {
        StateChannel stateChannel = new StateChannel(state, converter, receptor);
        stateChannels.put(state.getIdentifier(), stateChannel);
        state.withConsumer(this::onState);
    }

    protected void onState(EnvState state, EnvSignal<?> signal) {
        StateChannel stateChannel = stateChannels.get(state.getIdentifier());
        Converter<EnvSignal<?>, Float> converter = stateChannel.getConverter();
        SnnReceptor receptor = stateChannel.getReceptor();

        if (converter != null && receptor != null) {
            float convertedValue = converter.convert(signal);
            receptor.perceive(convertedValue);
        }
    }

    // --- (EnvFeedback, EnvSignal) → (SnnFeedback, Float) ---

    public void registerEnvFeedback(EnvFeedback envFeedback, Converter<EnvSignal<?>, Float> converter, SnnFeedback snnFeedback) {
        FeedbackChannel feedbackChannel = new FeedbackChannel(envFeedback, converter, snnFeedback);
        feedbackChannels.put(envFeedback.getIdentifier(), feedbackChannel);
        envFeedback.withConsumer(this::onFeedback);
    }

    protected void onFeedback(EnvFeedback envFeedback, EnvSignal<?> signal) {
        FeedbackChannel feedbackChannel = feedbackChannels.get(envFeedback.getIdentifier());
        Converter<EnvSignal<?>, Float> converter = feedbackChannel.getConverter();
        SnnFeedback snnFeedback = feedbackChannel.getSnnFeedback();

        if (converter != null && snnFeedback != null) {
            float value = converter.convert(signal);
            snnFeedback.perceive(value);
        }
    }

    // --- (SnnEffector, Float) → (EnvAction, EnvSignal) ---

    public void registerEffector(SnnEffector effector, Converter<Float, EnvSignal<?>> transformer, EnvAction envAction) {
        EffectorChannel effectorChannel = new EffectorChannel(effector, transformer, envAction);
        effectorChannels.put(effector.getIdentifier(), effectorChannel);
        effector.withConsumer(this::onEffector);
    }

    protected void onEffector(SnnEffector effector, float value) {
        EffectorChannel effectorChannel = effectorChannels.get(effector.getIdentifier());
        Converter<Float, EnvSignal<?>> converter = effectorChannel.getConverter();
        EnvAction envAction = effectorChannel.getAction();

        if (converter != null && envAction != null) {
            EnvSignal<?> signal = converter.convert(value);
            envAction.invoke(signal);
        }
    }
}
