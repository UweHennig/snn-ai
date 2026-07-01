/**
 * @(#)DendritPersistence.java
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
 * DendritPersistence
 *
 * @author Uwe Hennig
 */
public class DendritPersistence {
    public static void save(DendritModel model, Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, CREATE, READ, WRITE, TRUNCATE_EXISTING)) {
            int size = (int) model.segment.byteSize();

            try (Arena tempArena = Arena.ofConfined()) {
                MemorySegment mappedFile = channel.map(FileChannel.MapMode.READ_WRITE, 0, size, tempArena);
                mappedFile.copyFrom(model.segment);
                mappedFile.force();
            }
        }

    }

    public static DendritModel laod(Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, READ)) {
            int size = (int) channel.size();
            int capacity = size / (int) DendritModel.LAYOUT.byteSize();
            DendritModel model = new DendritModel(capacity);

            try (Arena tempArena = Arena.ofConfined()) {
                MemorySegment mappedFile = channel.map(FileChannel.MapMode.READ_ONLY, 0, size, tempArena);
                model.segment.copyFrom(mappedFile);
            }

            return model;
        }

    }

}
