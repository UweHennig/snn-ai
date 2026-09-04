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
    private static final int STATE_FREE    = 0;
    private static final int STATE_WRITING = 1;
    private static final int STATE_WAITING = 2;
    private static final int STATE_READING = 3;

    private static final int CELL_TARGET_ID_POS   = 0;
    private static final int CELL_TARGET_TYPE_POS = 1;
    private static final int HEAD_INTAKE_POS      = 0;

    private final MatrixModel model;
    private final int         index;

    public ReceptorView(MatrixModel model, int index) {
        this.model = model;
        this.index = index;
    }

    // --- Matrix cell operations ---
    public int getTargetId(int row, int col) {
        return model.getCellInt(index, row, col, CELL_TARGET_ID_POS);
    }

    public void setTargetId(int row, int col, int id) {
        model.setCellInt(index, row, col, CELL_TARGET_ID_POS, id);
    }

    public int getTargetType(int row, int col) {
        return model.getCellInt(index, row, col, CELL_TARGET_TYPE_POS);
    }

    public void setTargetType(int row, int col, int type) {
        model.setCellInt(index, row, col, CELL_TARGET_TYPE_POS, type);
    }

    // --- Header data ---
    public void setIntakeDistance(float value) {
        model.setHeaderFloat(index, HEAD_INTAKE_POS, value);
    }

    public float getIntakeDistance() {
        return model.getHeaderFloat(index, HEAD_INTAKE_POS);
    }

    // --- Meta data ---
    public int getCapacity() {
        return model.getCapacity();
    }

    public int getNumHeaders() {
        return model.getNumHeaders(index);
    }

    public int getNumRows() {
        return model.getNumRows(index);
    }

    public int getNumColumns() {
        return model.getNumColumns(index);
    }

    public int getNumSlotsPerCell() {
        return model.getNumSlotsPerCell(index);
    }

    public MatrixModel getModel() {
        return model;
    }
}
