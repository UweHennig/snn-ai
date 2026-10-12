/// @(#)StimulusDispatcherTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.neuron;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.mockito.Mockito;

import com.uwe_hennig.snn.util.BufferedTransferModel;
import com.uwe_hennig.snn.util.IntQueue;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// StimulusDispatcherTest
///
/// @author Uwe Hennig
public class StimulusDispatcherTest {

    @Test
    @DisplayName("StimulusDispatcherTest")
    public void testStimulusDispatcher() {
        BufferedTransferModel ts = null;
        IntQueue queue = null;
        StimulusDispatcher realDispatcher = null;
        StimulusDispatcher dispatcher = null;

        try {
            ts = new BufferedTransferModel(1048576);
            queue = new IntQueue(1024);
            realDispatcher = StimulusDispatcher.init(3, ts, queue);
            dispatcher = Mockito.spy(realDispatcher);

            doNothing().when(dispatcher).transfer(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyFloat());

            int blockA = ts.claimSlot(2, 3, 10, 20);
            int blockB = ts.claimSlot(2, 3, 30, 40);

            queue.offer(blockA);
            queue.offer(blockB);

            ts.setTrgId(blockA, 0, 60);
            ts.setTrgType(blockA, 0, 70);
            ts.setTrgId(blockA, 1, 60);
            ts.setTrgType(blockA, 1, 70);

            ts.setTrgId(blockB, 0, 80);
            ts.setTrgType(blockB, 0, 90);
            ts.setTrgId(blockB, 1, 100);
            ts.setTrgType(blockB, 1, 110);

            dispatcher.start();
            for (int i = 0; i < 100; i += 10) {
                ts.offerFbTime(blockA, 0, i);
                ts.offerFbValue(blockA, 0, i + 1.0f);
                ts.offerStimulus(blockA, 0, i + 2.0f);
                ts.offerFbTime(blockA, 1, i + 3.0f);
                ts.offerFbValue(blockA, 1, i + 4.0f);
                ts.offerStimulus(blockA, 1, i + 5.0f);

                ts.offerFbTime(blockB, 0, i + 6.0f);
                ts.offerFbValue(blockB, 0, i + 7.0f);
                ts.offerStimulus(blockB, 0, i + 8.0f);
                ts.offerFbTime(blockB, 1, i + 9.0f);
                ts.offerFbValue(blockB, 1, i + 10.0f);
                ts.offerStimulus(blockB, 1, i + 11.0f);

                Thread.sleep(Duration.ofMillis(5L));
            }
            Thread.sleep(Duration.ofSeconds(1L));
            dispatcher.stop(100);

            verify(dispatcher, atLeast(120)).transfer(anyInt(), anyInt(), anyInt(), anyInt(), anyInt(), anyFloat());

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        } finally {
            if (dispatcher != null) {
                dispatcher.stop(100);
            }
            if (ts != null) {
                ts.close();
            }
            if (queue != null) {
                queue.close();
            }
            SNNLogger.setActive(false);
            SNNLogger.setCallback(null);
        }
    }

    public final class Blackhole {
        private static long         liveness;
        public static volatile long SINK;

        public static void consume(float f) {
            liveness += Float.floatToRawIntBits(f);
        }

        public static void consume(boolean b) {
            liveness += 1;
        }

        public static void end() {
            SINK = liveness;

            if (SINK == System.nanoTime()) {
                System.out.print("This will almost never happen" + SINK);
            }

            liveness = 0L;
        }
    }

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}
