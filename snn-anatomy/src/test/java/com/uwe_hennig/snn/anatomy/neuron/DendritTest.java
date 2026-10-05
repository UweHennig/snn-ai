///  @(#)DendritTest.java
///  Copyright (c) 2026 Uwe Hennig
//   All rights reserved.
package com.uwe_hennig.snn.anatomy.neuron;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

///  DendritTest
///
///  @author Uwe Hennig
public class DendritTest {

    @Test
    @DisplayName("Simple DendritModel Test")
    public void testDendritModel() {
        final int totalSize = DendritModel.calculateSize(1);
        DendritModel model = new DendritModel(totalSize);

        checkModel(model, totalSize);
        int offset = model.alloc(1, 2, 3, 4);

        try {
            model.writeLock(offset);

            model.setFieldId(offset, 1);
            model.setNeuronId(offset, 2);
            model.setSomaId(offset, 3);
            model.setWeightId(offset, 4);
        } finally {
            model.writeUnlock(offset);
        }

        try {
            model.readLock(offset);

            assertEquals(1, model.getFiedlId(offset), "Invalid field id");
            assertEquals(2, model.getNeuronId(offset), "Invalid neuron id");
            assertEquals(3, model.getSomaId(offset),"invalid soma id");
            assertEquals(4, model.getWeighId(offset),"invalid weight id");
        } finally {
            model.readUnlock(offset);
        }

        model.close();
    }

    private void checkModel(DendritModel model, int totalSize) {
        System.out.printf("%nData information");
        System.out.printf("%nTotal size     : %2d bytes", model.getTotalSize());
        System.out.printf("%nEntry size     : %2d bytes", model.getEntrySize());
        System.out.printf("%nEntries        : %2d", model.getEntryCount());
        System.out.printf("%nUnused entries : %2d", model.getMaxEntries() - model.getEntryCount());

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertTrue((model.getMaxEntries() - model.getEntryCount()) >= 0, "Invalid number of entries");

        assertEquals(totalSize, model.getTotalSize(), "Invalid total size");
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

}
