/**
 * @(#)StimulusTest.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * StimulusTest
 * @formatter:off
 * @formatter:on
 * @author Uwe Hennig
 */
public class StimulusTest {

    @Test
    @DisplayName("Simple Stimulus Data Test")
    public void testData() {
        StimulusModel model = new StimulusModel(1);
        checkModel(model, 1);

        long time = System.nanoTime();
        model.setExpiry(0, time);
        assertEquals(time, model.getExpiry(0));

        model.setSrcIndex(0, 1);
        assertEquals(1, model.getSrcIndex(0));

        model.setTrgIndex(0, 2);
        assertEquals(2, model.getTrgIndex(0));

        model.setType(0, 3);
        assertEquals(3, model.getType(0));

        model.setValue(0, 4f);
        assertEquals(4f, model.getValue(0));

        model.close();
    }

    @Test
    @DisplayName("Simple claim Stimulus Test")
    public void testClaimUpdateAndTTL() {
        StimulusModel model = new StimulusModel(128);
        StimulusView view = new StimulusView(0, model, 10_000_000L);

        long now = System.nanoTime();

        for (int i = 0; i < model.getCapacity(); i++) {
            model.setExpiry(i, now - 1);
        }

        // ---- Claim a free Slot ----
        int idx = view.claimStimulus(1, 2, 3, 4.5f);
        assertTrue(idx >= 0, "Claim must return a valid index");

        long expiry = model.getExpiry(idx);
        assertTrue(expiry > now, "Expiry must be set to now + TTL");

        assertEquals(1, model.getSrcIndex(idx));
        assertEquals(2, model.getTrgIndex(idx));
        assertEquals(3, model.getType(idx));
        assertEquals(4.5f, model.getValue(idx));

        // ---- Update within TTL ----
        boolean ok = view.updateStimulus(idx, 10, 20, 30, 40.5f);
        assertTrue(ok, "Update must succeed while TTL is valid");

        assertEquals(10, model.getSrcIndex(idx));
        assertEquals(20, model.getTrgIndex(idx));
        assertEquals(30, model.getType(idx));
        assertEquals(40.5f, model.getValue(idx));

        long newExpiry = model.getExpiry(idx);
        assertTrue(newExpiry > expiry, "Update must extend TTL");

        // ---- TTL to drain artificially ----
        model.setExpiry(idx, System.nanoTime() - 1);

        boolean fail = view.updateStimulus(idx, 99, 99, 99, 99f);
        assertFalse(fail, "Update must fail after TTL expired");

        model.close();
    }

    @Test
    @DisplayName("Claim with skiped Stimulus Test")
    void testClaimSkipsLockedSlots() {
        StimulusModel model = new StimulusModel(32);
        StimulusView view = new StimulusView(0, model, 10_000_000L);

        long now = System.nanoTime();

        // Unlock all slots
        for (int i = 0; i < model.getCapacity(); i++) {
            model.setExpiry(i, now - 1);
        }

        // set slot 0..4 to locked
        for (int i = 0; i < 5; i++) {
            model.tryLock(i);
        }

        int idx = view.claimStimulus(1, 1, 1, 1f);

        assertTrue(idx > 4, "Claim must skip locked slot");

        model.close();
    }

    @BeforeEach
    public void beforeEach(TestInfo info) {
        String title = "### " + info.getDisplayName() + " ###";
        System.out.println("\n" + title);
        System.out.println("-".repeat(title.length()));
    }

    private void checkModel(StimulusModel model, int capacity) {
        System.out.printf("%nModel information");
        System.out.printf("%nCapacity       : %6d", model.capacity);
        System.out.printf("%nLayout size    : %6d bytes", StimulusModel.LAYOUT.byteSize());
        System.out.printf("%nByte size      : %6d bytes", model.segment.byteSize());
        System.out.printf("%nStructure      : %s%n%n", StimulusModel.LAYOUT);

        assertNotNull(model.arena, "Arena is null!");
        assertNotNull(model.segment, "Segment is null!");
        assertNotNull(model.sequenceLayout, "SequenceLayout is null!");

        assertEquals(capacity, model.capacity, "Invalid capacity!");
        assertEquals(StimulusModel.LAYOUT.byteSize() * model.capacity, model.segment.byteSize(), "Invalid segment size!");
    }

}
