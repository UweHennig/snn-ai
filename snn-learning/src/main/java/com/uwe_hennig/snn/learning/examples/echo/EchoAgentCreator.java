/**
 * @(#)EchoAgent.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.learning.examples.echo;

import java.time.Duration;
import java.util.List;

import com.uwe_hennig.snn.contracts.peripheral.AgentCreator;
import com.uwe_hennig.snn.contracts.peripheral.Converter;
import com.uwe_hennig.snn.contracts.peripheral.EnvAction;
import com.uwe_hennig.snn.contracts.peripheral.EnvFeedback;
import com.uwe_hennig.snn.contracts.peripheral.EnvSignal;
import com.uwe_hennig.snn.contracts.peripheral.EnvState;
import com.uwe_hennig.snn.contracts.peripheral.NeuroPeripheral;
import com.uwe_hennig.snn.contracts.peripheral.SnnEffector;
import com.uwe_hennig.snn.contracts.peripheral.SnnFeedback;
import com.uwe_hennig.snn.contracts.peripheral.SnnReceptor;
import com.uwe_hennig.snn.peripheral.agent.AgentMediator;

/**
 * EchoAgent
 * is a mediator that performs the transformation with the SNN so that the SNN can develop an echo response.
 *
 * @author Uwe Hennig
 */
public class EchoAgentCreator extends AgentMediator implements AgentCreator<EchoAgentCreator> {
    private EchoEnvironment environment;
    private NeuroPeripheral neuroPeripheral;

    @Override
    public void createEnvironment() {
        this.environment = new EchoEnvironment(Duration.ofSeconds(5), Duration.ofMillis(100));
    }

    @Override
    public void createEffectorMapping() {
        List<SnnEffector> effectors = neuroPeripheral.getEffectors();
        List<EnvAction> actions = environment.getEnvActionsPorts();

        if (actions.size() != effectors.size()) {
            throw new RuntimeException("Effector and action lists must have same size!");
        }

        for (int i = 0; i < actions.size(); i++) {
            EnvAction action = actions.get(i);
            SnnEffector effector = effectors.get(i);

            Converter<Float, EnvSignal<?>> transformer = d -> new EchoSignal((int) (d + 70)/14);
            registerEffector(effector, transformer, action);

            effector.withConsumer(super::onEffector);
        }
    }

    @Override
    public void createStateMapping() {
        List<SnnReceptor> receptors = neuroPeripheral.getReceptors();
        List<EnvState> states = environment.getEnvStatePorts();

        if (receptors.size() != states.size()) {
            throw new RuntimeException("Receptor and state lists must have same size!");
        }

        for (int i = 0; i < states.size(); i++) {
            EnvState state = states.get(i);
            SnnReceptor receptor = receptors.get(i);

            Converter<EnvSignal<?>, Float> converter = signal -> (Integer)signal.data() * 7.0f;
            registerState(state, converter, receptor);

            state.withConsumer(super::onState);
        }

    }

    @Override
    public void createFeedbackMapping() {
        List<SnnFeedback> snnFeedbacks = neuroPeripheral.getFeedbacks();
        List<EnvFeedback> envFeedbacks = environment.getEnvFeedbackPorts();

        if (snnFeedbacks.size() != envFeedbacks.size()) {
            throw new RuntimeException("SnnFeedback and EnvFeedback lists must have same size!");
        }

        for (int i = 0; i < envFeedbacks.size(); i++) {
            EnvFeedback envFeedback = envFeedbacks.get(i);
            SnnFeedback snnFeedback = snnFeedbacks.get(i);
            Converter<EnvSignal<?>, Float> converter = signal  -> (Integer)signal.data() * 1.0f;
            registerEnvFeedback(envFeedback, converter, snnFeedback);

            envFeedback.withConsumer(super::onFeedback);
        }
    }

    @Override
    public void loadServices() {
        // TODO Auto-generated method stub class AgentCreator<EchoAgentCreator>
    }

    @Override
    public void createNeuronField() {
        // TODO Auto-generated method stub class AgentCreator<EchoAgentCreator>
    }

    @Override
    public void createConnectome() {
        // TODO Auto-generated method stub class AgentCreator<EchoAgentCreator>
    }


}
