/// @(#)WeightTest.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestMethodOrder;

/// WeightTest
///
/// @author Uwe Hennig
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WeightTest {
    private static final int        RW_ENTRIES    = 1_000_000;
    private static final int        RW_TOTAL_SIZE = WeightModel.calculateSize(1_000_000);
    private static final AtomicLong counter       = new AtomicLong();

    @Test
    @DisplayName("Simple MemoryModel Test")
    public void testMemoryModel() {
        WeightModel model = null;
        try {
            int size = WeightModel.calculateSize(1);
            model = new WeightModel(size);
            checkModel(model, size);
            int offset = model.alloc();

            assertEquals(WeightModel.META_SIZE, offset);

            model.setWeight(offset, 1f);
            assertEquals(1f, model.getWeight(offset), "invalid weight in model");

            model.setPreSynapticTime(offset, 2f);
            assertEquals(2f, model.getPreSynapticTime(offset), "invalid preSynapticTime in model");

            model.setPostSynapticTime(offset, 3f);
            assertEquals(3f, model.getPostSynapticTime(offset), "invalid postSynapticTime in model");

            model.setHebbTimeRange(offset, 4f);
            assertEquals(4f, model.getHebbTimeRange(offset), "invalid hebbTimeRange in model");

            model.setHebbScale(offset, 5f);
            assertEquals(5f, model.getHebbScale(offset), "invalid hebbScale in model");

            model.setWeightScale(offset, 6f);
            assertEquals(6f, model.getWeightScale(offset), "invalid weightScale in model");

            model.setTimeLimit(offset, 7f);
            assertEquals(7f, model.getTimeLimit(offset), "invalid timeLimit in model");
        } finally {
            if (model != null) {
                model.close();
            }
        }
    }

    @Test
    @Order(1)
    @DisplayName("Write to File Test")
    public void testWrite() {
        final int totalSize = WeightModel.calculateSize(RW_ENTRIES);
        final WeightModel model = new WeightModel(totalSize);
        float values = 0f;
        System.out.printf("Writing %,d records (%,d bytes)%n", RW_ENTRIES, totalSize);

        try {
            for (int i = 0; i < RW_ENTRIES; i++) {
                int offset = model.alloc();

                model.setWeight(offset, values);
                model.setHebbTimeRange(offset, values + 1f);
                values += 2f;
            }

            Path path = getArenaPath("weight.data");

            long startTime = System.nanoTime();
            WeightPersistence.save(model, path);
            long endTime = System.nanoTime();

            System.out.printf("Written %,d bytes %n", model.segment.byteSize());
            System.out.printf("Written in %,3.2f milli sec%n%n", (endTime - startTime) / 1_000_000f);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        } finally {
            model.close();
        }
    }

    @Test
    @Order(2)
    @DisplayName("Read from File Test")
    public void testRead() {
        WeightModel model = null;
        try {
            System.out.printf("Read %,d records!%n", RW_TOTAL_SIZE);
            Path path = getArenaPath("weight.data");
            assertTrue(path.toFile().exists(), "file not created " + path);

            long startTime = System.nanoTime();
            model = WeightPersistence.load(path);
            long endTime = System.nanoTime();

            assertNotNull(model, "Model not created!");

            int entryCount = model.getEntryCount();

            float values = 0f;
            for (int i = 0; i < entryCount; i++) {
                int offset = model.offsetFromIndex(i);

                assertEquals(values, model.getWeight(offset));
                assertEquals(values + 1f, model.getHebbTimeRange(offset));
                values += 2;
            }

            System.out.printf("Read %,d bytes %n", model.segment.byteSize());
            System.out.printf("Read in %,3.1f milli sec%n%n", (endTime - startTime) / 1_000_000f);

            model.close();
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getLocalizedMessage());
        }
    }

    private void checkModel(WeightModel model, int totalSize) {
        System.out.printf("%nData information");
        System.out.printf("%nTotal size     : %2d bytes", model.getTotalSize());
        System.out.printf("%nEntry size     : %2d bytes", model.getEntrySize());
        System.out.printf("%nEntries        : %2d", model.getEntryCount());
        System.out.printf("%nUnused entries : %2d", model.getMaxEntries() - model.getEntryCount());

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertTrue((model.getMaxEntries() - model.getEntryCount()) >= 0, "Invalid number of entries");

        assertEquals(totalSize, model.getTotalSize(), "Invalid total size");
        System.out.println();
    }

    private static Path getArenaPath(String fileName) {
        String envPath = System.getenv("SNN_STORAGE_PATH");
        if (envPath != null) {
            return Path.of(envPath, fileName);
        }

        Path localPath = Path.of(System.getProperty("user.dir"), "data", "arena", fileName);

        try {
            Files.createDirectories(localPath.getParent());
        } catch (IOException ignored) {
        }

        return localPath;
    }

    @AfterEach
    public void afterEach() {
        counter.set(0);
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    @AfterAll
    public static void clean() {
        Path path = getArenaPath("weight.data");
        if (path.toFile().exists()) {
            path.toFile().delete();
        }
    }

}
