/**
 * @(#)Test.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.junit.jupiter.api.Test;

/**
 * Test
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class MemoryLockTest {
    private static final GroupLayout LAYOUT = MemoryLayout.structLayout(
        ValueLayout.JAVA_INT.withName("lock"),
        ValueLayout.JAVA_INT.withName("head")
    );

    private static final VarHandle VH_LOCK = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lock"));
    private static final VarHandle VH_HEAD = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("head"));

    void lock(MemorySegment ptr) {
        int spins = 0;
        while (!VH_LOCK.compareAndSet(ptr, 0L, 0, 1)) {
            if (spins < 64) {
                Thread.onSpinWait();
                spins++;
            } else {
                LockSupport.parkNanos(1);
            }
        }
    }

    void unlock(MemorySegment ptr) {
        VH_LOCK.setRelease(ptr, 0L, 0);
    }

    private static volatile boolean running = true;

    @Test
    public void brutalTest() throws InterruptedException {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment ptr = arena.allocate(LAYOUT);
            AtomicLong counter = new AtomicLong(0);

            VH_LOCK.set(ptr, 0L, 0);
            VH_HEAD.set(ptr, 0L, 0);

            // THREAD A: The "owner" of the lock
            Thread threadA = new Thread(() -> {
                System.out.println("Thread A Started (using internal lock)...");
                lock(ptr);
                while (running) {
                    counter.incrementAndGet();
                    VH_HEAD.set(ptr, 0L, 1000);
                    int value = (int)VH_HEAD.get(ptr, 0);
                    if (value != 1000) {
                        System.err.println("A: " + counter.get() + " " + VH_HEAD.get(ptr, 0));
                    } else {
                        System.out.println("A: " + counter.get() + " " + VH_HEAD.get(ptr, 0));
                    }
                }
                unlock(ptr);
            });

            // THREAD B: "The Attacker" (Brutal Violence at HEAD)
            ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
            Thread threadB = new Thread(() -> {
                System.out.println("Started Thread B (ignoring internal lock)...");
                while (running) {
                    try {
                        lock.writeLock().lock();
                        counter.incrementAndGet();
                        VH_HEAD.set(ptr, 0L, 2000);
                        int value = (int)VH_HEAD.get(ptr, 0);
                        if (value != 1000) {
                            System.err.println("B: " + counter.get() + " " + VH_HEAD.get(ptr, 0));
                        } else {
                            System.out.println("B: " + counter.get() + " " + VH_HEAD.get(ptr, 0));
                        }
                    } finally {
                        lock.writeLock().unlock();
                    }
                }
            });

            threadA.start();
            threadB.start();

            // 5 Seconds of pure chaos in the storage room
            Thread.sleep(5);
            running = false;

            threadA.join();
            threadB.join();

            System.out.println("------------------------------------");
            System.out.println("Result after 2 seconds of fighting:");
            System.out.println("Lock field (Offset 0): " + VH_LOCK.get(ptr, 0L));
            System.out.println("HEAD       (Offset 4): " + VH_HEAD.get(ptr, 0L));
            System.out.println("------------------------------------");
        }
    }
}
