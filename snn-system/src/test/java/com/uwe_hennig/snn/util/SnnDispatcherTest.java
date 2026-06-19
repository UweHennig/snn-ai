/**
 * @(#)SnnDispatcherTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

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
    public void testSimple() {
        AtomicLong counter = new AtomicLong();
        SnnDispatcher realDispatcher = SnnDispatcher.of(1, 1, 1);
        SnnDispatcher dispatcher = Mockito.spy(realDispatcher);

        Mockito.doAnswer(invocation -> {
            int id = invocation.getArgument(0);
            counter.incrementAndGet();
            System.out.printf("%n %4d: processed %d", counter.get(), id);
            return null;
        }).when(dispatcher).doIt(Mockito.anyInt());

        dispatcher.start();

        for (int i = 0; i < 10; i++) {
            dispatcher.offer(i);
        }

        dispatcher.stop();

        dispatcher.shutdown();
    }

    @Test
    @DisplayName("Asynchronous access test")
    public void testConcurrent() {
        try {
            final int numberOfThreads = 12;
            ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads);

            AtomicLong counter = new AtomicLong();
            SnnDispatcher realDispatcher = SnnDispatcher.of(8, 4, 2);
            SnnDispatcher dispatcher = Mockito.spy(realDispatcher);
            Mockito.doAnswer(invocation -> {
                invocation.getArgument(0);
                counter.incrementAndGet();
                return null;
            }).when(dispatcher).doIt(Mockito.anyInt());

            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch finishLatch = new CountDownLatch(numberOfThreads);

            for (int i = 0; i < numberOfThreads; i++) {
                final int n = i;
                executor.submit(() -> {
                    try {
                        startLatch.await();
                        dispatcher.offer(n);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    } finally {
                        finishLatch.countDown();
                    }
                });
            }
            startLatch.countDown();
            boolean completed = finishLatch.await(5, TimeUnit.SECONDS);
            assertTrue(completed, "The test timed out!");
            dispatcher.stop();
            dispatcher.shutdown();
        } catch (InterruptedException e) {
            e.printStackTrace();
            fail("Exception in testConcurrent: " + e.getLocalizedMessage());
        }
    }
}
