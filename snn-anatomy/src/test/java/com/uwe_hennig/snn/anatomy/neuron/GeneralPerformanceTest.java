/// @(#)GeneralPerformanceTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static java.lang.foreign.ValueLayout.JAVA_FLOAT;
import static java.lang.foreign.ValueLayout.JAVA_INT;

import java.lang.foreign.Arena;
import java.lang.foreign.GroupLayout;
import java.lang.foreign.MemoryLayout;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.SequenceLayout;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/// GeneralPerformanceTest
///
/// @author Uwe Hennig
public class GeneralPerformanceTest {
    public Arena arena;

    static final GroupLayout LAYOUT = MemoryLayout
        .structLayout(JAVA_INT.withName("fieldA"), JAVA_INT.withName("fieldB"), JAVA_FLOAT.withName("fieldC"), JAVA_FLOAT.withName("fieldD"))
        .withByteAlignment(8);

    static final VarHandle VH_FIELD_A = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fieldA"));
    static final VarHandle VH_FIELD_B = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fieldB"));
    static final VarHandle VH_FIELD_C = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fieldC"));
    static final VarHandle VH_FIELD_D = LAYOUT.arrayElementVarHandle(MemoryLayout.PathElement.groupElement("fieldD"));

    static SequenceLayout sequenceLayout = MemoryLayout.sequenceLayout(1000, LAYOUT);

    private MemorySegment memorySegmentLayout;
    private MemorySegment memorySegmentCalculation;

    @Test
    @DisplayName("Performance Layout")
    public void measurementLayoutPerformance() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        final int loops = 10_000_000;
        long operations = 0L;
        int index;

        long start = System.nanoTime();
        for (int i = 0; i < loops; i++) {
            index = rand.nextInt(1000);
            setFieldA_L(index, rand.nextInt(10));
            setFieldB_L(index, rand.nextInt(10));
            setFieldC_L(index, rand.nextFloat(10.0f));
            setFieldD_L(index, rand.nextFloat(10.0f));

            Blackhole.consume(getFieldA_L(index));
            Blackhole.consume(getFieldB_L(index));
            Blackhole.consume(getFieldB_L(index));
            Blackhole.consume(getFieldB_L(index));
            operations += 8;
        }
        long end = System.nanoTime();
        Blackhole.end();

        long totalNs = end - start;
        double nsPerOp = (double) totalNs / operations;
        double opsPerSec = 1_000_000_000.0 / nsPerOp;

        System.out.printf("Operations     : %,13d ops%n", operations);
        System.out.printf("Throughput     : %,13.2f ops/s%n", opsPerSec);
        System.out.printf("Latency        : %,6.2f ns/op%n", nsPerOp);
    }

    @Test
    @DisplayName("Performance Offset")
    public void measurementOffsetPerformance() {
        ThreadLocalRandom rand = ThreadLocalRandom.current();
        final int loops = 10_000_000;
        long operations = 0L;
        int index;

        long start = System.nanoTime();
        for (int i = 0; i < loops; i++) {
            index = rand.nextInt(1000) * 16;
            setFieldA_O(index, rand.nextInt(10));
            setFieldB_O(index, rand.nextInt(10));
            setFieldC_O(index, rand.nextFloat(10.0f));
            setFieldD_O(index, rand.nextFloat(10.0f));

            Blackhole.consume(getFieldA_O(index));
            Blackhole.consume(getFieldB_O(index));
            Blackhole.consume(getFieldB_O(index));
            Blackhole.consume(getFieldB_O(index));
            operations += 8;
        }
        long end = System.nanoTime();
        Blackhole.end();

        long totalNs = end - start;
        double nsPerOp = (double) totalNs / operations;
        double opsPerSec = 1_000_000_000.0 / nsPerOp;

        System.out.printf("Operations     : %,13d ops%n", operations);
        System.out.printf("Throughput     : %,13.2f ops/s%n", opsPerSec);
        System.out.printf("Latency        : %,6.2f ns/op%n", nsPerOp);
    }

    // ---

    void setFieldA_L(int index, int value) {
        VH_FIELD_A.set(memorySegmentLayout, 0L, index, value);
    }

    int getFieldA_L(int index) {
        return (int) VH_FIELD_A.get(memorySegmentLayout, 0, index);
    }

    void setFieldB_L(int index, int value) {
        VH_FIELD_B.set(memorySegmentLayout, 0L, index, value);
    }

    int getFieldB_L(int index) {
        return (int) VH_FIELD_B.get(memorySegmentLayout, 0L, index);
    }

    void setFieldC_L(int index, float value) {
        VH_FIELD_C.set(memorySegmentLayout, 0L, index, value);
    }

    float getFieldC_L(int index) {
        return (float) VH_FIELD_C.get(memorySegmentLayout, 0L, index);
    }

    void setFieldD_L(int index, float value) {
        VH_FIELD_D.set(memorySegmentLayout, 0L, index, value);
    }

    float getFieldD_L(int index) {
        return (float) VH_FIELD_D.get(memorySegmentLayout, 0L, index);
    }

    // ---

    void setFieldA_O(int offset, int value) {
        memorySegmentCalculation.set(ValueLayout.JAVA_INT, offset, value);
    }

    int getFieldA_O(int offset) {
        return memorySegmentCalculation.get(ValueLayout.JAVA_INT, offset);
    }

    void setFieldB_O(int offset, int value) {
        memorySegmentCalculation.set(ValueLayout.JAVA_INT, offset + 4, value);
    }

    int getFieldB_O(int offset) {
        return memorySegmentCalculation.get(ValueLayout.JAVA_INT, offset + 4);
    }

    void setFieldC_O(int offset, float value) {
        memorySegmentCalculation.set(ValueLayout.JAVA_FLOAT, offset + 8, value);
    }

    float getFieldC_O(int offset) {
        return memorySegmentCalculation.get(ValueLayout.JAVA_FLOAT, offset + 8);
    }

    void setFieldD_O(int offset, float value) {
        memorySegmentCalculation.set(ValueLayout.JAVA_FLOAT, offset + 12, value);
    }

    float getFieldD_O(int offset) {
        return memorySegmentCalculation.get(ValueLayout.JAVA_FLOAT, offset + 12);
    }

    // ---

    public final class Blackhole {
        private static long         liveness;
        public static volatile long SINK;

        public static void consume(float f) {
            liveness += Float.floatToRawIntBits(f);
        }

        public static void consume(int b) {
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
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));

        this.arena = Arena.ofShared();
        memorySegmentLayout = arena.allocate(sequenceLayout);
        memorySegmentCalculation = arena.allocate(1000 * 16);
    }

    @AfterEach
    public void afterEach() {
        if (arena != null) {
            arena.close();
        }
    }

}
