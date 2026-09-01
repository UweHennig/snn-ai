/**
 * @(#)EventQueueTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * EventQueueTest
 *
 * @author Uwe Hennig
 */
public class EventQueueTest {
    private EventQueue queue;

    @Test
    @DisplayName("Simple FIFO test")
    void testBasicFIFO() {
        queue.enqueue(1, 2);
        queue.enqueue(3, 4);
        queue.enqueue(5, 6);

        int[] values = queue.dequeue();
        assertNotNull(values);
        assertEquals(1, values[0], "FIFO rule violated 1");
        assertEquals(2, values[1], "FIFO rule violated 2");

        values = queue.dequeue();
        assertNotNull(values);
        assertEquals(3, values[0], "FIFO rule violated 3");
        assertEquals(4, values[1], "FIFO rule violated 4");

        values = queue.dequeue();
        assertNotNull(values);
        assertEquals(5, values[0], "FIFO rule violated 5");
        assertEquals(6, values[1], "FIFO rule violated 6");
    }

    @Test
    @DisplayName("Event performance test")
    void performanceTest() {
        final int loops = 1_000_000;
        final ThreadLocalRandom rand = ThreadLocalRandom.current();

        long numOps = 0L;
        long start = System.nanoTime();
        for (int i = 0; i < loops; i++) {
            for (int j=0; j < 100; j++) {
                queue.enqueue(rand.nextInt(), rand.nextInt());
                int [] result = queue.dequeue();
                Blackhole.consume(result[0]);
                Blackhole.consume(result[1]);
                numOps+=3; // 1 write, 2 reads
            }
        }
        long end = System.nanoTime();

        double sec = (end - start) / numOps;
        double avgOpsPerSec = numOps / sec;

        System.out.println();
        System.out.println("EventQueue read/write test: ");
        System.out.printf("Operations : %,6d%n", numOps);
        System.out.printf("Throughput : %,6.2f ops/sec%n", avgOpsPerSec);
        System.out.printf("Latency    : %,13.2f ns/op%n", numOps / avgOpsPerSec);
    }

    @BeforeEach
    void setUp(TestInfo info) {
        queue = new EventQueue(65536);

        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterEach
    void tearDown() {
        queue.close();
    }

    public final class Blackhole {
        @SuppressWarnings("unused")
        private static int SINK;

        public static void consume(int v) {
            SINK = v;
            if ((v & 0x1) == 0x1) {
                /* noop */
            }
        }

        public static int getSink() {
            return SINK;
        }
    }
}
