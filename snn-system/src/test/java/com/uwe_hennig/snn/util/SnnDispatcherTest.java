/**
 * @(#)SnnDispatcherTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

/**
 * SnnDispatcherTest
 *
 * @author Uwe Hennig
 */
public class SnnDispatcherTest {
    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @Test
    @DisplayName("Simple SnnDispatcher test")
    public void simpleTest() {
        // int ingestQueueSize, int workerQueueSize, int workerThreadSize
        SnnDispatcher realDispatcher = new SnnDispatcher(1, 1, 1);
        SnnDispatcher dispatcher = Mockito.spy(realDispatcher);

        Mockito.doAnswer(invocation -> {
            int id = invocation.getArgument(0);
            System.out.println("MOCKED doIt: " + id);
            return null;
        }).when(dispatcher).doIt(Mockito.anyInt());

        dispatcher.start();

        for (int i = 0; i < 10; i++) {
            dispatcher.offer(i);
        }

        dispatcher.stop();

        dispatcher.shutdown();

    }
}
