/**
 * @(#)WeightPersistence.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.neuron;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.READ;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.nio.channels.FileChannel;
import java.nio.file.Path;

/**
 * WeightPersistence
 * @author Uwe Hennig
 */
public final class WeightPersistence {
    static void save(WeightModel model, Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, CREATE, READ, WRITE,TRUNCATE_EXISTING)) {
            long size = model.segment.byteSize();

            try (Arena tempArena = Arena.ofConfined()) {
                MemorySegment mappedFile = channel.map(FileChannel.MapMode.READ_WRITE, 0, size, tempArena);
                mappedFile.copyFrom(model.segment);
                mappedFile.force();
            }
        }
    }

    static WeightModel load(Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, READ)) {
            long size = channel.size();
            long capacity = size / WeightModel.LAYOUT.byteSize();
            WeightModel model = new WeightModel(capacity);

            try (Arena tempArena = Arena.ofConfined()) {
                MemorySegment mappedFile = channel.map(FileChannel.MapMode.READ_ONLY, 0, size, tempArena);
                model.segment.copyFrom(mappedFile);
            }

            return model;
        }
    }
}
