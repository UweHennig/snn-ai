/**
 * @(#)EchoEnvironment.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.learning.examples.echo;

import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

import com.uwe_hennig.snn.afferent.agent.EnvActionImpl;
import com.uwe_hennig.snn.afferent.agent.EnvFeedbackImpl;
import com.uwe_hennig.snn.afferent.agent.EnvStateImpl;
import com.uwe_hennig.snn.contracts.afferent.EnvAction;
import com.uwe_hennig.snn.contracts.afferent.EnvFeedback;
import com.uwe_hennig.snn.contracts.afferent.EnvFeedbackType;
import com.uwe_hennig.snn.contracts.afferent.EnvSignal;
import com.uwe_hennig.snn.contracts.afferent.EnvState;
import com.uwe_hennig.snn.learning.contracts.Environment;
import com.uwe_hennig.snn.util.Trigger;

/**
 * EchoEnvironment generates random numbers between 0 and 10 and checks the SNN's response.
 *
 * @author Uwe Hennig
 */
public class EchoEnvironment implements Environment {
    private final Random rand = new Random(System.currentTimeMillis());

    private EnvState    statePort         = EnvStateImpl.of(1);
    private EnvAction   actionPort        = EnvActionImpl.of(2, this::onAction);
    private EnvFeedback valueFeedbackPort = EnvFeedbackImpl.of(3, EnvFeedbackType.VALUE);
    private EnvFeedback timeFeedbackPort  = EnvFeedbackImpl.of(4, EnvFeedbackType.TIME);

    private AtomicBoolean pendingAnswer = new AtomicBoolean(false);

    private Trigger  trigger;
    private Duration totalRuntime;
    private Duration stepInterval;

    volatile int state =0;

    public EchoEnvironment(Duration totalRuntime, Duration stepInterval) {
        this.totalRuntime = totalRuntime;
        this.stepInterval = stepInterval;
    }

    @Override
    public void onAction(EnvAction action, EnvSignal<?> signal) {
        if (pendingAnswer.compareAndSet(true, false)) {
            // received in time
            Integer actionValue = (Integer) signal.data();

            EnvSignal<Integer> valueFeedbackSignal = new EchoSignal(state - actionValue);
            valueFeedbackPort.invoke(valueFeedbackSignal);
        }
    }

    private void sendData() {
        if (pendingAnswer.compareAndSet(true, false)) {
            // not in time
            EnvSignal<Integer> timeFeedbackSignal = new EchoSignal(10);
            timeFeedbackPort.invoke(timeFeedbackSignal);
        }

        // next test
        state = rand.nextInt(10) + 1;
        EnvSignal<Integer> stateSignal = new EchoSignal(state);
        statePort.invoke(stateSignal);
        pendingAnswer.getAndSet(true);
    }

    @Override
    public void start() {
        trigger = Trigger.of(stepInterval, this::sendData);
        trigger.startWithTotalRuntime(totalRuntime);
    }

    @Override
    public void stop() {
        trigger.stop();
    }


    @Override
    public List<EnvAction> getEnvActionsPorts() {
        return List.of(actionPort);
    }

    @Override
    public List<EnvState> getEnvStatePorts() {
        return List.of(statePort);
    }

    @Override
    public List<EnvFeedback> getEnvFeedbackPorts() {
        return List.of(valueFeedbackPort, timeFeedbackPort);
    }

}
