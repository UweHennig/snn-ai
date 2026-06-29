/**
 * @(#)EchoApp.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.learning.examples.echo;

import java.time.Duration;

/**
 * EchoApp
 *
 * @author Uwe Hennig
 */
public class EchoApp {
    private EchoAgent agent;

    public void start(Duration totalRuntime) {
        agent = new EchoAgent().create();
        agent.start(Duration.ofSeconds(5));
    }

    public static void main(String[] args) {

    }
}
