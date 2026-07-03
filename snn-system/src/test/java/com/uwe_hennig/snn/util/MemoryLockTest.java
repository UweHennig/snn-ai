/**
 * @(#)MemoryLockTest.java
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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * MemoryLockTest
 *
 * @author Uwe Hennig
 */
public class MemoryLockTest {

    private static final GroupLayout LAYOUT = MemoryLayout.structLayout(
            ValueLayout.JAVA_INT.withName("lock"),
            ValueLayout.JAVA_INT.withName("head")
    );

    private static final VarHandle VH_LOCK = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("lock"));
    private static final VarHandle VH_HEAD = LAYOUT.varHandle(MemoryLayout.PathElement.groupElement("head"));

    private static volatile boolean running = true;

    // --- WRITE LOCK ---
    boolean writeLock(MemorySegment ptr) {
        int spins = 0;
        while (!VH_LOCK.compareAndSet(ptr, 0L, 0, -1) && spins < 100) {
            if (spins < 64) {
                Thread.onSpinWait();
            } else {
                LockSupport.parkNanos(1);
            }
            spins++;
        }
        return spins < 100;
    }

    void writeUnlock(MemorySegment ptr) {
        int current = (int) VH_LOCK.getVolatile(ptr, 0L);
        if (current == -1) {
            VH_LOCK.setRelease(ptr, 0L, 0);
        }
    }

    // --- READ LOCK ---
    public boolean tryReadLock(MemorySegment ptr) {
        int current = (int) VH_LOCK.getVolatile(ptr, 0L);
        if (current < 0) {
            return false;
        }

        return VH_LOCK.compareAndSet(ptr, 0L, current, current + 1);
    }

    boolean readLock(MemorySegment segment) {
        int spins = 0;
        while (spins < 100) {
            int current = segment.get(ValueLayout.JAVA_INT, 0);
            current = (int) VH_LOCK.getVolatile(segment, 0L);

            if (current >= 0) {
                if (VH_LOCK.compareAndSet(segment, 0L, current, current + 1)) {
                    return true;
                }
                continue;
            }

            if (spins < 64) {
                Thread.onSpinWait();
            } else {
                LockSupport.parkNanos(1);
            }
            spins++;
        }
        return false;
    }

    void readUnlock(MemorySegment ptr) {
        while (true) {
            int current = (int) VH_LOCK.getVolatile(ptr, 0L);
            if (current <= 0) {
                return;
            }

            if (VH_LOCK.compareAndSet(ptr, 0L, current, current - 1)) {
                return;
            }
            Thread.onSpinWait();
        }
    }

    @Test
    @DisplayName("Read/Write Lock Test")
    public void testReadWriteLock() throws InterruptedException {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment ptr = arena.allocate(LAYOUT);
            AtomicLong tests = new AtomicLong(0);
            AtomicLong writeMisses = new AtomicLong(0);
            AtomicLong readMisses = new AtomicLong(0);

            VH_LOCK.set(ptr, 0L, 0);
            VH_HEAD.set(ptr, 0L, 0);
            running = true;

            Thread writeLockThread = new Thread(() -> {
                while (running) {
                    tests.incrementAndGet();
                    if (writeLock(ptr)) {
                        VH_HEAD.set(ptr, 0L, 1000);
                        writeUnlock(ptr);
                    } else {
                        writeMisses.incrementAndGet();
                    }
                }
            });

            Thread readLockThread = new Thread(() -> {
                while (running) {
                    tests.incrementAndGet();
                    if (readLock(ptr)) {
                        int val = (int) VH_HEAD.getVolatile(ptr, 0L);
                        readUnlock(ptr);
                    } else {
                        readMisses.incrementAndGet();
                    }
                }
            });

            writeLockThread.start();
            readLockThread.start();

            Thread.sleep(2000);
            running = false;

            writeLockThread.join();
            readLockThread.join();

            System.out.println("Tests total: " + tests.get());
            System.out.println("Missed Writes: " + writeMisses.get());
            System.out.println("Missed Reads: " + readMisses.get());
        }
    }

    @Test
    @DisplayName("Brutal Chaos Test")
    public void brutalTest() throws InterruptedException {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment ptr = arena.allocate(LAYOUT);
            AtomicLong counter = new AtomicLong(0);
            running = true;

            VH_LOCK.set(ptr, 0L, 0);
            VH_HEAD.set(ptr, 0L, 0);

            Thread threadA = new Thread(() -> {
                while (running) {
                    if (writeLock(ptr)) {
                        counter.incrementAndGet();
                        VH_HEAD.set(ptr, 0L, 1000);
                        writeUnlock(ptr);
                    }
                }
            });

            Thread threadB = new Thread(() -> {
                while (running) {
                    VH_HEAD.set(ptr, 0L, 2000);
                    counter.incrementAndGet();
                }
            });

            threadA.start();
            threadB.start();

            Thread.sleep(1000);
            running = false;

            threadA.join();
            threadB.join();

            System.out.println("Chaos Test ended. Counter: " + counter.get());
        }
    }

    @Test
    @DisplayName("Test with other Lock")
    public void testWithOtherLocks() throws InterruptedException {
        try (Arena arena = Arena.ofShared()) {
            MemorySegment ptr = arena.allocate(LAYOUT);
            AtomicLong counter = new AtomicLong(0);

            VH_LOCK.set(ptr, 0L, 0);
            VH_HEAD.set(ptr, 0L, 0);

            // THREAD A: The "owner" of the lock
            Thread threadA = new Thread(() -> {
                System.out.println("Thread A Started (using internal lock)...");
                writeLock(ptr);
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
                writeUnlock(ptr);
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
            Thread.sleep(1000);
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

    @BeforeEach
    void setUp(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }
}