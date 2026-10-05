/// @(#)AxonPersistence.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
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

/// AxonPersistence
///
/// @author Uwe Hennig
///
public class AxonPersistence {
    public static void save(AxonModel model, Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, CREATE, READ, WRITE, TRUNCATE_EXISTING)) {
            int size = (int) model.segment.byteSize();

            try (Arena tempArena = Arena.ofConfined()) {
                MemorySegment mappedFile = channel.map(FileChannel.MapMode.READ_WRITE, 0, size, tempArena);
                mappedFile.copyFrom(model.segment);
                mappedFile.force();
            }
        }

    }

    public static AxonModel load(Path file) throws IOException {
        return new AxonModel(file);
    }
}
