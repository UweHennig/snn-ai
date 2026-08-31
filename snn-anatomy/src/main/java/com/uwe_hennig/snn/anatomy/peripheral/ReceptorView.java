/**
 * @(#)ReceptorView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import com.uwe_hennig.snn.util.MatrixModel;
import com.uwe_hennig.snn.util.TapeModel;

/**
 * ReceptorView
 *
 * @author Uwe Hennig
 */
public final class ReceptorView {
    private static final int STATE_FREE     = 0;
    private static final int STATE_WRITING  = 1;
    private static final int STATE_WAITING  = 2;
    private static final int STATE_READING  = 3;


    private static final int CELL_TARGET_ID_POS = 0;
    private static final int CELL_TARGET_TYPE_POS = 1;
    private static final int HEAD_INTAKE_POS = 0;

    private final MatrixModel model;
    private final TapeModel tape;

    public ReceptorView(MatrixModel model, TapeModel tape) {
        this.model = model;
        this.tape = tape;
    }

    // --- Tape operations ---

    // returns block
    public int claimFreeBlock() {
        int blocks = tape.getCapacity(0);
        for (int i=0; i<blocks;i++) {
           if (tape.setStatus(blocks, STATE_FREE, STATE_WRITING)) {
               return i;
           }
        }
        return -1;
    }

    public boolean publishBlock(int block) {
        return tape.setStatus(block, STATE_WRITING, STATE_WAITING);
    }

    // returns block
    public int claimWaitingBlock() {
        int blocks = tape.getCapacity(0);
        for (int i=0; i<blocks;i++) {
           if (tape.setStatus(blocks, STATE_FREE, STATE_WRITING)) {
               return i;
           }
        }
        return -1;
    }

    public boolean releaseSlot(int slot) {
        return tape.setStatus(slot, STATE_READING, STATE_FREE);
    }

    public void setStimulusType(int block, long index, int type) {
        // TODO die Prüfungen außerhalb!
        if (index < 0 || index >= tape.getCapacity(block)) {
            throw new IndexOutOfBoundsException("Tape index outside the capacity");
        }
        assert tape.getStatus(block) == STATE_WRITING : "Illegal write access to block " + block;

        tape.setStimulusType(block, index, type);
    }

    public int getStimulusType(int block, long index) {
        // TODO die Prüfungen außerhalb!
        if (index < 0 || index >= tape.getCapacity(block)) {
            throw new IndexOutOfBoundsException("Tape index outside the capacity");
        }
        assert tape.getStatus(block) == STATE_READING : "Illegal read access to block " + block;
        return tape.getStimulusType(block, index);
    }

    // TODO more!

    // --- Matrix cell operations ---
    public int getTargetId(int index, int row, int col) {
        return model.getCellInt(index, row, col, CELL_TARGET_ID_POS);
    }

    public void setTargetId(int index, int row, int col, int id) {
        model.setCellInt(index, row, col, CELL_TARGET_ID_POS, id);
    }

    public int getTargetType(int index, int row, int col) {
        return model.getCellInt(index, row, col, CELL_TARGET_TYPE_POS);
    }

    public void setTargetType(int index, int row, int col, int type) {
        model.setCellInt(index, row, col, CELL_TARGET_TYPE_POS, type);
    }

    // --- Header data ---
    public void setIntakeDistance(int index, float value) {
        model.setHeaderFloat(index, HEAD_INTAKE_POS, value);
    }

    public float getIntakeDistance(int index) {
        return model.getHeaderFloat(index, HEAD_INTAKE_POS);
    }

    // --- Meta data ---
    public int getCapacity() {
        return model.getCapacity();
    }

    public int getNumHeaders() {
        return model.getNumHeaders();
    }

    public int getNumRows() {
        return model.getNumRows();
    }

    public int getNumColumns() {
        return model.getNumColumns();
    }

    public int getNumSlotsPerCell() {
        return model.getNumSlotsPerCell();
    }

    public MatrixModel getModel() {
        return model;
    }
}
