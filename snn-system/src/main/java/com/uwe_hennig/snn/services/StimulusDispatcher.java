/// @(#)StimulusDispatcher.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.services;

import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.util.BufferedTransferSegment;
import com.uwe_hennig.snn.util.IntQueue;
import com.uwe_hennig.snn.util.logging.SNNLogger;

/// StimulusDispatcher
///
/// The StimulusDispatcher retrieves an offset from the offset queue, locates the corresponding block in the
/// BufferTransferSegment, and transfers the values found there along the call graph. Once processing is complete, the
/// offset is placed back into the offset queue.
///
/// @author Uwe Hennig
public final class StimulusDispatcher {
    private static StimulusDispatcher INSTANCE;

    private final BufferedTransferSegment buffer;
    private final IntQueue                bufferOffsets;
    private final int                     numWorkers;

    private final ExecutorService executor;
    private final AtomicBoolean   isRunning = new AtomicBoolean(false);

    private StimulusDispatcher(int numWorkers, BufferedTransferSegment buffer, IntQueue bufferOffsets) {
        this.buffer = buffer;
        this.bufferOffsets = bufferOffsets;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.numWorkers = numWorkers;
    }

    public static StimulusDispatcher init(int numWorkers, BufferedTransferSegment buffer, IntQueue bufferOffsets) {
        if (INSTANCE == null) {
            synchronized (StimulusDispatcher.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StimulusDispatcher(numWorkers, buffer, bufferOffsets);
                }
            }
        }
        return INSTANCE;
    }

    public void start() {
        if (!isRunning.compareAndSet(false, true)) {
            return;
        }

        for (int i = 0; i < numWorkers; i++) {
            executor.submit(() -> {
                try {
                    while (isRunning.get() && !Thread.currentThread().isInterrupted()) {
                        int offset = bufferOffsets.poll();
                        if (offset >= 0) {
                            dispatch(offset);
                            bufferOffsets.offer(offset);
                        } else {
                            LockSupport.parkNanos(1);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    String msg = "Error in StimulusDispatcher: " + e.getLocalizedMessage();
                    System.err.println(msg);
                    throw new RuntimeException(msg);
                }
            });
        }
    }

    public boolean stop(long timeoutMillis) {
        if (!isRunning.compareAndSet(true, false)) {
            return true;
        }

        try {
            executor.shutdown();
            if (executor.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
                return true;
            } else {
                executor.shutdownNow();
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            executor.shutdownNow();
            return false;
        }
    }

    private void dispatch(int offset) {
        int entries = buffer.getEntries(offset);
        for (int entry = 0; entry < entries; entry++) {
            int srcId = buffer.getSrcId(offset);
            int srcType = buffer.getSrcType(offset);

            int trgId = buffer.getTrgId(offset, entry);
            int trgType = buffer.getTrgType(offset, entry);

            float stimulus = buffer.pollStimulus(offset, entry);
            if (!Float.isNaN(stimulus)) {
                transfer(StimulusType.STIMULUS, srcId, srcType, trgId, trgType, stimulus);
            }

            float fbTime = buffer.pollFbTime(offset, entry);
            if (!Float.isNaN(fbTime)) {
                transfer(StimulusType.TIME_FEEDBACK, srcId, srcType, trgId, trgType, fbTime);
            }

            float fbValue = buffer.pollFbValue(offset, entry);
            if (!Float.isNaN(fbValue)) {
                transfer(StimulusType.VALUE_FEEDBACK, srcId, srcType, trgId, trgType, fbValue);
            }
        }

        // Once complete, the block must be made available again for processing within the segment.
        bufferOffsets.offer(offset);
    }

    private void transfer(StimulusType type, int srcId, int srcType, int trgId, int trgType, float value) {
        SNNLogger.debug(() -> String.format(Locale.ENGLISH, "%3d -> %3d (%3.2f)", srcId, trgId, value));
    }

    public static StimulusDispatcher instance() {
        return INSTANCE;
    }

}
