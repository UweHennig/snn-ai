/// @(#)CompileResult.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.contracts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/// CompileResult
///
/// @author Uwe Hennig
///
public final class CompileResult{
    public record CerebroInfo(long totalBytes, long totalFields, long totalNeurons, long totalDendrites, long totalSynapses) {}

    private final List<String> errors = new ArrayList<>();
    private long totalBytes;
    private long totalFields;
    private long totalNeurons;
    private long totalDendrites;
    private long totalSynapses;

    public void addNeuron(long bytes) {
        totalNeurons++;
        totalBytes += bytes;
    }

    public void addField(long bytes) {
        totalFields++;
        totalBytes += bytes;
    }

    public void addDendrite(long bytes) {
        totalDendrites++;
        totalBytes += bytes;
    }

    public void addSynapse(long bytes) {
        totalSynapses++;
        totalBytes += bytes;
    }

    public void addError(String error) {
        errors.add(error);
    }

    public boolean isSuccess() {
        return errors.isEmpty();
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }

    public void reset() {
        this.errors.clear();
        this.totalBytes = 0;
        this.totalFields = 0;
        this.totalNeurons = 0;
        this.totalDendrites = 0;
        this.totalSynapses = 0;
    }

    public CerebroInfo toInfo() {
        return new CerebroInfo(totalBytes, totalFields, totalNeurons, totalDendrites, totalSynapses);
    }
}
