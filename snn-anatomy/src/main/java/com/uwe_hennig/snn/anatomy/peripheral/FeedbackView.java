/**
 * @(#)FeedbackView.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.anatomy.peripheral;

import com.uwe_hennig.snn.anatomy.allocator.FeedbackModelManager;

/**
 * FeedbackView
 *
 * @author Uwe Hennig
 */
public class FeedbackView {
    // ----- lock/unlock -----

    public static void readLock(int index) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        model.readLock(index);
    }

    public static void readUnlock(int index) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        model.readUnlock(index);
    }

    public static void writeLock(int index) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        model.writeLock(index);
    }

    public static void writeUnlock(int index) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        model.writeUnlock(index);
    }

    // ----- getter/setter -----

    public static int getTemporalFilterIndex(int index) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        return model.getTemporalFilterIndex(index);
    }

    public static void setTemporalFilterIndex(int index, int value) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        model.setTemporalFilterIndex(index, value);
    }

    public static int getInformationFilterIndex(int index) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        return model.getInformationFilterIndex(index);
    }

    public static void setInformationFilterIndex(int index, int value) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        model.setInformationFilterIndex(index, value);
    }

    public static int getRelatedNeuronElementRef(int index) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        return model.getRelatedNeuronElementRef(index);
    }

    public static void setRelatedNeuronElementRef(int index, int value) {
        FeedbackModel model = FeedbackModelManager.instance().getModel();
        model.setRelatedNeuronElementRef(index, value);
    }


}
