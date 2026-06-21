/**
 * @(#)MultiList.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.util;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * MultiList
 * TODO Outsource persistence
 * @author Uwe Hennig
 */
public class MultiList implements AutoCloseable {
    private static final long FILE_META_TAIL_PTR = 0;
    private static final long FIRST_BLOCK_OFFSET = 8;

    private static final long HEADER_COUNT_VAL = 0;
    private static final long HEADER_NEXT_ADDR = 8;
    private static final long BLOCK_DATA_START = 16;

    private static final long NO_NEXT_BLOCK = -1L;

    private final Lock[]     lockStripes;
    private static final int STRIPE_COUNT = 1024;

    private final Arena         arena;
    private final MemorySegment writeSegment;
    private final MemorySegment readSegment;
    private final AtomicLong    tail;

    private final int  blockSize;
    private final int  dataCapacityBytes;
    private final long maxFileSize;

    // --- API ---

    public MultiList(Path path, long maxBlocks, int dataCapacityBytes) throws IOException {
        this.dataCapacityBytes = dataCapacityBytes;
        this.blockSize = (int) ((BLOCK_DATA_START + dataCapacityBytes + 7) & ~7);

        this.maxFileSize = FIRST_BLOCK_OFFSET + (maxBlocks * blockSize);

        boolean isNewFile = !Files.exists(path) || Files.size(path) == 0;
        this.arena = Arena.ofShared();

        try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            this.writeSegment = channel.map(FileChannel.MapMode.READ_WRITE, 0, maxFileSize, arena);
            this.readSegment = writeSegment.asReadOnly();
        }

        if (isNewFile) {
            this.tail = new AtomicLong(FIRST_BLOCK_OFFSET);
            persistMeta(FIRST_BLOCK_OFFSET);
        } else {
            long savedTail = writeSegment.get(ValueLayout.JAVA_LONG, FILE_META_TAIL_PTR);
            this.tail = new AtomicLong(Math.max(savedTail, FIRST_BLOCK_OFFSET));
        }

        this.lockStripes = new ReentrantLock[STRIPE_COUNT];
        for (int i = 0; i < STRIPE_COUNT; i++) {
            lockStripes[i] = new ReentrantLock();
        }
    }

    public long allocate() {
        long newBlockAddress = tail.getAndAdd(blockSize);

        if (newBlockAddress + blockSize > maxFileSize) {
            throw new IllegalStateException("File size exceeded! Logical tail reached max allowed blocks.");
        }

        // Persistence: Update the new high water mark in the file header
        persistMeta(newBlockAddress + blockSize);

        // Initialise header of new block
        writeSegment.set(ValueLayout.JAVA_LONG, newBlockAddress + HEADER_COUNT_VAL, 0L);
        writeSegment.set(ValueLayout.JAVA_LONG, newBlockAddress + HEADER_NEXT_ADDR, NO_NEXT_BLOCK);

        return newBlockAddress;
    }

    public void put(long startAddress, int[] data) {
        writeGeneric(startAddress, data, ValueLayout.JAVA_INT);
    }

    public void put(long startAddress, byte[] data) {
        writeGeneric(startAddress, data, ValueLayout.JAVA_BYTE);
    }

    public void put(long startAddress, long[] data) {
        writeGeneric(startAddress, data, ValueLayout.JAVA_LONG);
    }

    public void put(long startAddress, float[] data) {
        writeGeneric(startAddress, data, ValueLayout.JAVA_FLOAT);
    }

    public void put(long startAddress, String string) {
        put(startAddress, string.getBytes(StandardCharsets.US_ASCII));
    }

    public int[] readInts(long startAddress) {
        return (int[]) readGeneric(startAddress, ValueLayout.JAVA_INT);
    }

    public long[] readLongs(long startAddress) {
        return (long[]) readGeneric(startAddress, ValueLayout.JAVA_LONG);
    }

    public byte[] readBytes(long startAddress) {
        return (byte[]) readGeneric(startAddress, ValueLayout.JAVA_BYTE);
    }

    public float[] readFloat(long startAddress) {
        return (float[]) readGeneric(startAddress, ValueLayout.JAVA_FLOAT);
    }

    public String readString(long startAddress) {
        return new String(readBytes(startAddress), StandardCharsets.US_ASCII);
    }

    // --- Engine ---

    private void writeGeneric(long startAddress, Object array, ValueLayout layout) {
        if (array == null || startAddress < FIRST_BLOCK_OFFSET) {
            return;
        }

        Lock lock = getLock(startAddress);
        lock.lock();

        try {
            int totalElements = java.lang.reflect.Array.getLength(array);
            int elementSize = (int) layout.byteSize();
            int elementsPerBlock = dataCapacityBytes / elementSize;

            long currentBlockAddr = startAddress;
            int writtenElements = 0;

            while (writtenElements < totalElements) {
                int count = Math.min(totalElements - writtenElements, elementsPerBlock);
                boolean hasMore = (writtenElements + count < totalElements);

                writeSegment.set(ValueLayout.JAVA_LONG, currentBlockAddr + HEADER_COUNT_VAL, count);
                MemorySegment.copy(array, writtenElements, writeSegment, layout, currentBlockAddr + BLOCK_DATA_START, count);
                writtenElements += count;

                if (hasMore) {
                    long nextAddrInHeader = writeSegment.get(ValueLayout.JAVA_LONG, currentBlockAddr + HEADER_NEXT_ADDR);

                    if (nextAddrInHeader < FIRST_BLOCK_OFFSET || nextAddrInHeader == NO_NEXT_BLOCK) {
                        long newBlockAddr = allocate();
                        writeSegment.set(ValueLayout.JAVA_LONG, currentBlockAddr + HEADER_NEXT_ADDR, newBlockAddr);
                        currentBlockAddr = newBlockAddr;
                    } else {
                        currentBlockAddr = nextAddrInHeader;
                    }
                } else {
                    writeSegment.set(ValueLayout.JAVA_LONG, currentBlockAddr + HEADER_NEXT_ADDR, NO_NEXT_BLOCK);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private Object readGeneric(long startAddress, ValueLayout layout) {
        if (startAddress < FIRST_BLOCK_OFFSET || startAddress >= maxFileSize) {
            return java.lang.reflect.Array.newInstance(layout.carrier(), 0);
        }

        Lock lock = getLock(startAddress);
        lock.lock();
        try {
            List<Object> fragments = new ArrayList<>();
            int totalFound = 0;
            long currentAddr = startAddress;

            while (currentAddr != NO_NEXT_BLOCK && currentAddr < maxFileSize && currentAddr >= FIRST_BLOCK_OFFSET) {
                int count = (int) readSegment.get(ValueLayout.JAVA_LONG, currentAddr + HEADER_COUNT_VAL);
                long nextAddr = readSegment.get(ValueLayout.JAVA_LONG, currentAddr + HEADER_NEXT_ADDR);

                if (count > 0) {
                    Object block = java.lang.reflect.Array.newInstance(layout.carrier(), count);
                    MemorySegment.copy(readSegment, layout, currentAddr + BLOCK_DATA_START, block, 0, count);
                    fragments.add(block);
                    totalFound += count;
                }
                currentAddr = nextAddr;
            }

            Object result = java.lang.reflect.Array.newInstance(layout.carrier(), totalFound);
            int pos = 0;
            for (Object fragment : fragments) {
                int len = java.lang.reflect.Array.getLength(fragment);
                System.arraycopy(fragment, 0, result, pos, len);
                pos += len;
            }
            return result;
        } finally {
            lock.unlock();
        }
    }

    private Lock getLock(long address) {
        int index = (int) (Math.abs(address / blockSize) % STRIPE_COUNT);
        return lockStripes[index];
    }

    private void persistMeta(long currentTail) {
        writeSegment.set(ValueLayout.JAVA_LONG, FILE_META_TAIL_PTR, currentTail);
    }

    @Override
    public void close() {
        if (arena != null && arena.scope().isAlive()) {
            try {
                writeSegment.force();
            } catch (Exception e) {
                System.err.println("Error saving data: " + e.getMessage());
            } finally {
                arena.close();
            }
        }
    }

}
