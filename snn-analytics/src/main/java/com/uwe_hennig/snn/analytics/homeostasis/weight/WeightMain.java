/**
 * @(#)WeightMain.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.analytics.homeostasis.weight;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import com.uwe_hennig.snn.analytics.ui.GraphPlotter;
import com.uwe_hennig.snn.anatomy.allocator.WeightModelManager;
import com.uwe_hennig.snn.anatomy.neuron.WeightModel;

/**
 * WeightMain
 *
 * @author Uwe Hennig
 */
public class WeightMain {
    public static final String WEIGHT_FUNCTION_NAME   = "weight";
    public static final String STIMULUS_FUNCTION_NAME = "stimulus";
    public static final String FEEDBACK_FUNCTION_NAME = "feedback";

    private GraphPlotter plotter;
    private WeightModel  model;

    private int   index;
    private float time;

    public WeightMain() {
        initUi();
        initOffheap();
    }

    public void addWeight(float time, float weight) {
        plotter.addPoint(WEIGHT_FUNCTION_NAME, weight, weight);
    }

    public void run() {
    }

    private void initUi() {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        this.plotter = GraphPlotter.frame(0.75, 0.5, () -> close())
            .withXRange(0.0, 200.0, 20)
            .withYRange(-70, 70, 14)
            .addFunction(WEIGHT_FUNCTION_NAME, Color.blue)
            //            .addFunction(STIMULUS_FUNCTION_NAME, Color.red)
            //            .addFunction(FEEDBACK_FUNCTION_NAME, Color.green)
            .build();
    }

    private void initOffheap() {
        WeightModelManager manager = WeightModelManager.init(1);
        model = manager.getModel();
        index = manager.nextId();
    }

    private void close() {
        if (model != null) {
            model.close();
        }
    }

    public static void main(String[] args) {
        WeightMain main = new WeightMain();
        main.run();
    }

}
