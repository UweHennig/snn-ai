/**
 * @(#)WeightMain.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.analytics.homeostasis.weight;

import java.awt.Color;
import java.util.concurrent.ThreadLocalRandom;

import com.uwe_hennig.snn.analytics.ui.GraphPlotter;
import com.uwe_hennig.snn.anatomy.allocator.WeightModelManager;
import com.uwe_hennig.snn.anatomy.neuron.WeightModel;
import com.uwe_hennig.snn.anatomy.neuron.WeightView;

/**
 * WeightMain
 *
 * @author Uwe Hennig
 */

public class WeightMain {
    public static final String POTENTIAL_FUNCTION_NAME = "potential";
    public static final String WEIGHT_FUNCTION_NAME    = "weight";
    public static final String FEEDBACK_FUNCTION_NAME  = "feedback";

    private static float TIMER_TICK = 0.1f;

    private GraphPlotter plotter;
    private WeightModel  model;

    private int     index;
    private boolean running;

    private float currentTime;

    public WeightMain() {
        initUi();
        initOffheap();
    }

    public void addWeight(float time, float weight) {
        plotter.addPoint(POTENTIAL_FUNCTION_NAME, weight, weight);
    }

    public void run() {
        running = true;
        while (running) {
            // Sleep has no effect on the calculations!
            sleep();
            currentTime += TIMER_TICK;
            if (rand(0.75f)) {
                calculateStimulus();
            } else {
                calculateFeedback();
            }
            calculateWeight();
        }
    }

    public void calculateFeedback() {
        if (running) {
            float deltaTimeFeedback = calculateFeedbackDT();
            float potential = WeightView.applyFeedback(index, deltaTimeFeedback);
            plotter.addPoint(POTENTIAL_FUNCTION_NAME, currentTime, potential);
        }
    }

    public void calculateStimulus() {
        if (running) {
            float receivingPotential = createStimulus();
            float potential = WeightView.applyStimulus(index, receivingPotential, currentTime);
            plotter.addPoint(POTENTIAL_FUNCTION_NAME, currentTime, potential);
        }
    }

    public void calculateWeight() {
        if (running) {
            float weight = WeightView.getWeight(index);
            plotter.addPoint(WEIGHT_FUNCTION_NAME, currentTime, weight);
        }
    }

    private void initUi() {
        this.plotter = GraphPlotter.frame(0.75, 0.5, () -> close())
            .withXRange(0.0, 20.0, 20)
            .withYRange(-5, 5, 10)
            .withLegend(true)
            .addFunction(POTENTIAL_FUNCTION_NAME, Color.blue)
            .addFunction(WEIGHT_FUNCTION_NAME, Color.red)
            .build();
    }

    private void initOffheap() {
        WeightModelManager manager = WeightModelManager.init(1);
        model = manager.getModel();
        index = manager.nextId();
        WeightView.initDefaultValues(index);
    }

    private void close() {
        running = false;
        if (model != null) {
            model.close();
        }
        System.out.println("\nclosed!");
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public float createStimulus() {
        return (float) Math.sin(currentTime) * 3.0f;
    }

    public float calculateFeedbackDT() {
        return 10.0f;
    }

    public boolean rand(float pct) {
        return ThreadLocalRandom.current().nextFloat() < pct;
    }

    public static void main(String[] args) {
        WeightMain main = new WeightMain();
        main.run();
    }

}
