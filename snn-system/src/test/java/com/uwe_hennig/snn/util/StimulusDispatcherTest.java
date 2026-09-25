/// @(#)StimulusDispatcherTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.fail;

import java.time.Duration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

import com.uwe_hennig.snn.services.StimulusDispatcher;

/// StimulusDispatcherTest
///
/// @author Uwe Hennig
public class StimulusDispatcherTest {
    @Test
    @DisplayName("StimulusDispatcherTest")
    public void testStimulusDispatcher() {
        BufferedTransferSegment ts = null;
        IntQueue queue = null;
        StimulusDispatcher dispatcher = null;

        try {
            ts = new BufferedTransferSegment(1048576);
            queue = new IntQueue(1024);
            dispatcher = StimulusDispatcher.init(3, ts, queue);

            int blockA = ts.allocateBlock(2, 10, 20);
            int blockB = ts.allocateBlock(2, 30, 40);

            queue.offer(blockA);
            queue.offer(blockB);

            ts.setTrgId(blockA, 0, 60);
            ts.setTrgType(blockA, 0, 70);
            ts.setTrgId(blockA, 1, 60);
            ts.setTrgType(blockA, 1, 70);

            ts.offerFbTime(blockA, 0, 1.0f);
            ts.offerFbValue(blockA, 0, 2.0f);
            ts.offerStimulus(blockA, 0, 3.0f);
            ts.offerFbTime(blockA, 1, 4.0f);
            ts.offerFbValue(blockA, 1, 5.0f);
            ts.offerStimulus(blockA, 1, 6.0f);


            ts.setTrgId(blockB, 0, 80);
            ts.setTrgType(blockB, 0, 90);
            ts.setTrgId(blockB, 1, 100);
            ts.setTrgType(blockB, 1, 110);

            ts.offerFbTime(blockA, 0, 7.0f);
            ts.offerFbValue(blockA, 0, 8.0f);
            ts.offerStimulus(blockA, 0, 9.0f);
            ts.offerFbTime(blockA, 1, 10.0f);
            ts.offerFbValue(blockA, 1, 11.0f);
            ts.offerStimulus(blockA, 1, 12.0f);

            dispatcher.start();
            Thread.sleep(Duration.ofSeconds(2L));

            dispatcher.stop(100);

        } catch (InterruptedException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        } finally {
            if (dispatcher != null) {
                dispatcher.stop(1000);
            }
            if (ts != null) {
                ts.close();
            }
            if (queue != null) {
                queue.close();
            }
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
