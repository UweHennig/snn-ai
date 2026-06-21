/**
 * @(#)MultiList.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import java.io.IOException;
import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.lang.invoke.VarHandle;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <b>MultiList</b> can store various lists of different lengths in memory.
 * The starting point offset of the list is returned by the method `allocate`.
 * This starting offset of a list should be stored in a domain object.
 * It is currently not possible to delete a single value or shorten entries.
 * @author Uwe Hennig
 */
public class MultiList {
    private static final long FILE_META_TAIL_PTR = 0;
    private static final long FILE_META_CAPACITY = 8;
    private static final long FIRST_BLOCK_OFFSET = 16;

    private static final long HEADER_COUNT_VAL = 0;
    private static final long HEADER_NEXT_ADDR = 8;
    private static final long BLOCK_DATA_START = 16;

    private static final long NO_NEXT_BLOCK = -1L;

    private final Lock[]     lockStripes;
    private static final int STRIPE_COUNT = 1024;

    private static final VarHandle VH_LONG = ValueLayout.JAVA_LONG.varHandle();

    private final Arena         arena;
    private final MemorySegment memorySegment;
    private final AtomicLong    tail;

    private final int  blockSize;
    private final int  dataCapacityBytes;
    private final long maxMemorySize;

    /**
     * Creates a new off-heap memory.
     *
     * @param maxBlocks Maximum number of blocks
     * @param dataCapacityBytes Capacity per block in bytes
     */
    public MultiList(long maxBlocks, int dataCapacityBytes) {
        this.dataCapacityBytes = dataCapacityBytes;

        this.blockSize = (int) ((BLOCK_DATA_START + dataCapacityBytes + 7) & ~7);
        this.maxMemorySize = FIRST_BLOCK_OFFSET + (maxBlocks * blockSize);

        this.arena = Arena.ofShared();
        this.memorySegment = arena.allocate(maxMemorySize, 8);

        this.tail = new AtomicLong(FIRST_BLOCK_OFFSET);
        memorySegment.set(ValueLayout.JAVA_LONG, FILE_META_CAPACITY, dataCapacityBytes);

        persistMeta(FIRST_BLOCK_OFFSET);

        this.lockStripes = new ReentrantLock[STRIPE_COUNT];
        for (int i = 0; i < STRIPE_COUNT; i++) {
            lockStripes[i] = new ReentrantLock();
        }
    }

    /**
     * Allocates a segment for a new array list
     * @return offset of stored array list
     */
    public long allocate() {
        long newBlockAddress = tail.getAndAdd(blockSize);

        if (newBlockAddress + blockSize > maxMemorySize) {
            throw new IllegalStateException("Off-Heap memory exhausted!");
        }

        // Update metadata
        persistMeta(newBlockAddress + blockSize);

        // Initialize header
        memorySegment.set(ValueLayout.JAVA_LONG, newBlockAddress + HEADER_COUNT_VAL, 0L);
        memorySegment.set(ValueLayout.JAVA_LONG, newBlockAddress + HEADER_NEXT_ADDR, NO_NEXT_BLOCK);

        return newBlockAddress;
    }

    // --- Write operations ---

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
        put(startAddress, string.getBytes(StandardCharsets.UTF_8));
    }

    // --- Read operations ---

    public int[] getInts(long startAddress) {
        return (int[]) readGeneric(startAddress, ValueLayout.JAVA_INT);
    }

    public long[] getLongs(long startAddress) {
        return (long[]) readGeneric(startAddress, ValueLayout.JAVA_LONG);
    }

    public byte[] getBytes(long startAddress) {
        return (byte[]) readGeneric(startAddress, ValueLayout.JAVA_BYTE);
    }

    public float[] getFloat(long startAddress) {
        return (float[]) readGeneric(startAddress, ValueLayout.JAVA_FLOAT);
    }

    public String getString(long startAddress) {
        return new String(getBytes(startAddress), StandardCharsets.UTF_8);
    }

    public void delete(long startAddress) {
        if (startAddress < FIRST_BLOCK_OFFSET || startAddress >= maxMemorySize) {
            return;
        }

        Lock lock = getLock(startAddress);
        lock.lock();
        try {
            long currentAddr = startAddress;

            while (currentAddr != NO_NEXT_BLOCK && currentAddr >= FIRST_BLOCK_OFFSET && currentAddr < maxMemorySize) {
                long nextAddr = memorySegment.get(ValueLayout.JAVA_LONG, currentAddr + HEADER_NEXT_ADDR);
                memorySegment.set(ValueLayout.JAVA_LONG, currentAddr + HEADER_COUNT_VAL, -1L);
                memorySegment.set(ValueLayout.JAVA_LONG, currentAddr + HEADER_NEXT_ADDR, NO_NEXT_BLOCK);
                currentAddr = nextAddr;
            }
        } finally {
            lock.unlock();
        }
    }

    // --- Persistence ---

    /**
     * Save stores the complete data to a file.
     * @param file is the path to a file
     * @throws IOException
     */
    public void save(Path file) throws IOException {
        persistMeta(tail.get());

        long size = memorySegment.byteSize();

        try (FileChannel channel = FileChannel.open(file,
                StandardOpenOption.CREATE,
                StandardOpenOption.READ,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            try (Arena tempArena = Arena.ofConfined()) {
                MemorySegment mappedFile = channel.map(FileChannel.MapMode.READ_WRITE, 0, size, tempArena);
                mappedFile.copyFrom(this.memorySegment);
                mappedFile.force();
            }
        }
    }

    /**
     * Load reads the data from a file and creates a complete memory image of Blockchain
     * @param file
     * @param dataCapacityBytes
     * @return
     * @throws IOException
     */
    public static MultiList load(Path file) throws IOException {
        try (FileChannel channel = FileChannel.open(file, StandardOpenOption.READ)) {
            long fileSize = channel.size();

            try (Arena tempArena = Arena.ofConfined()) {
                MemorySegment headerMap = channel.map(FileChannel.MapMode.READ_ONLY, 0, 16, tempArena);
                long savedTail = headerMap.get(ValueLayout.JAVA_LONG, FILE_META_TAIL_PTR);
                int savedCapacity = (int) headerMap.get(ValueLayout.JAVA_LONG, FILE_META_CAPACITY);

                int blockSize = (16 + savedCapacity + 7) & ~7;
                long maxBlocks = (fileSize - FIRST_BLOCK_OFFSET) / blockSize;

                MultiList storage = new MultiList(maxBlocks, savedCapacity);

                MemorySegment fullMap = channel.map(FileChannel.MapMode.READ_ONLY, 0, fileSize, tempArena);
                storage.memorySegment.copyFrom(fullMap);

                storage.tail.set(Math.max(savedTail, FIRST_BLOCK_OFFSET));
                return storage;
            }
        }
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

                memorySegment.set(ValueLayout.JAVA_LONG, currentBlockAddr + HEADER_COUNT_VAL, count);
                MemorySegment.copy(array, writtenElements, memorySegment, layout, currentBlockAddr + BLOCK_DATA_START, count);
                writtenElements += count;

                if (hasMore) {
                    long nextAddrInHeader = memorySegment.get(ValueLayout.JAVA_LONG, currentBlockAddr + HEADER_NEXT_ADDR);

                    if (nextAddrInHeader < FIRST_BLOCK_OFFSET || nextAddrInHeader == NO_NEXT_BLOCK) {
                        long newBlockAddr = allocate();

                        VH_LONG.setRelease(memorySegment, currentBlockAddr + HEADER_NEXT_ADDR, newBlockAddr);
                        currentBlockAddr = newBlockAddr;
                    } else {
                        currentBlockAddr = nextAddrInHeader;
                    }
                } else {
                    memorySegment.set(ValueLayout.JAVA_LONG, currentBlockAddr + HEADER_NEXT_ADDR, NO_NEXT_BLOCK);
                }
            }
        } finally {
            lock.unlock();
        }
    }

    private Object readGeneric(long startAddress, ValueLayout layout) {
        if (startAddress < FIRST_BLOCK_OFFSET || startAddress >= maxMemorySize) {
            return java.lang.reflect.Array.newInstance(layout.carrier(), 0);
        }

        Lock lock = getLock(startAddress);
        lock.lock();
        try {
            long firstCount = memorySegment.get(ValueLayout.JAVA_LONG, startAddress + HEADER_COUNT_VAL);
            if (firstCount == -1L) {
                return null;
            }

            List<Object> fragments = new ArrayList<>();
            int totalFound = 0;
            long currentAddr = startAddress;

            while (currentAddr != NO_NEXT_BLOCK && currentAddr < maxMemorySize && currentAddr >= FIRST_BLOCK_OFFSET) {
                int count = (int) memorySegment.get(ValueLayout.JAVA_LONG, currentAddr + HEADER_COUNT_VAL);
                long nextAddr = memorySegment.get(ValueLayout.JAVA_LONG, currentAddr + HEADER_NEXT_ADDR);

                if (count > 0) {
                    Object block = java.lang.reflect.Array.newInstance(layout.carrier(), count);
                    MemorySegment.copy(memorySegment, layout, currentAddr + BLOCK_DATA_START, block, 0, count);
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

    private void persistMeta(long newTail) {
        long current;
        do {
            current = (long) VH_LONG.getVolatile(memorySegment, FILE_META_TAIL_PTR);

            if (newTail <= current) {
                break;
            }

        } while ((long) VH_LONG.compareAndExchange(memorySegment, FILE_META_TAIL_PTR, current, newTail) != current);
    }

    public void close() {
        if (arena != null && arena.scope().isAlive()) {
            arena.close();
        }
    }
}