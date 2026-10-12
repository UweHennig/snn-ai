/// @(#)StimulusDispatcher.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.neuron;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

import com.uwe_hennig.snn.contracts.core.ElementType;
import com.uwe_hennig.snn.contracts.core.StimulusType;
import com.uwe_hennig.snn.util.BufferedTransferModel;
import com.uwe_hennig.snn.util.IntQueue;

/// StimulusDispatcher
///
/// The StimulusDispatcher retrieves an offset from the offset queue, locates the corresponding block in the
/// BufferTransferSegment, and transfers the values found there along the call graph. Once processing is complete, the
/// offset is placed back into the offset queue.
///
/// @author Uwe Hennig
public final class StimulusDispatcher {
    private static StimulusDispatcher INSTANCE;

    private final BufferedTransferModel buffer;
    private final IntQueue              bufferOffsets;
    private final int                   numWorkers;

    private final ExecutorService executor;
    private final AtomicBoolean   isRunning = new AtomicBoolean(false);

    private StimulusDispatcher(int numWorkers, BufferedTransferModel buffer, IntQueue bufferOffsets) {
        this.buffer = buffer;
        this.bufferOffsets = bufferOffsets;
        this.executor = Executors.newVirtualThreadPerTaskExecutor();
        this.numWorkers = numWorkers;
    }

    public static StimulusDispatcher init(int numWorkers, BufferedTransferModel buffer, IntQueue bufferOffsets) {
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
                        } else {
                            LockSupport.parkNanos(1);
                        }
                        // Once complete, the block must be made available again for processing within the segment.
                        bufferOffsets.offer(offset);
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
        // read header
        int entries = buffer.getEntries(offset);
        int srcType = buffer.getSrcType(offset);
        int srcNeuronId = buffer.getNeuronId(offset);

        // process entries
        for (int entry = 0; entry < entries; entry++) {
            float stimulusValue = buffer.pollStimulus(offset, entry);
            if (!Float.isNaN(stimulusValue)) {
                int trgId = buffer.getTrgId(offset, entry);
                int trgType = buffer.getTrgType(offset, entry);

                transfer(StimulusType.STIMULUS, srcNeuronId, srcType, trgId, trgType, stimulusValue);
            }

            float fbTime = buffer.pollFbTime(offset, entry);
            if (!Float.isNaN(fbTime)) {
                int trgId = buffer.getTrgId(offset, entry);
                int trgType = buffer.getTrgType(offset, entry);

                transfer(StimulusType.TIME_FEEDBACK, srcNeuronId, srcType, trgId, trgType, stimulusValue);
            }

            float fbValue = buffer.pollFbValue(offset, entry);
            if (!Float.isNaN(fbValue)) {
                int trgId = buffer.getTrgId(offset, entry);
                int trgType = buffer.getTrgType(offset, entry);

                transfer(StimulusType.VALUE_FEEDBACK, srcNeuronId, srcType, trgId, trgType, stimulusValue);
            }
        }
    }

    protected void transfer(int stimulusType, int srcNeuronId, int srcType, int trgId, int trgType, float stimulusValue) {
        if (ElementType.isDendrit(trgType)) {
            Dendrit.stimulate(trgId, srcNeuronId, srcType, stimulusType, stimulusValue);
        }

        if (ElementType.isSoma(trgType)) {
            Soma.stimulate(trgId, srcNeuronId, srcType, stimulusType, stimulusValue);
        }

        if (ElementType.isAxon(trgType)) {
            Axon.stimulate(trgId, srcNeuronId, srcType, stimulusType, stimulusValue);
        }

        if (ElementType.isSynapse(trgType)) {
            Synapse.stimulate(trgId, srcNeuronId, srcType, stimulusType, stimulusValue);
        }
    }

    public static StimulusDispatcher instance() {
        return INSTANCE;
    }

}
