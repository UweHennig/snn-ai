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
    public static final String STIMULUS_FUNCTION_NAME  = "stimulus";
    public static final String FEEDBACK_FUNCTION_NAME  = "feedback";

    private static float TIMER_TICK = 0.1f;

    private GraphPlotter plotter;
    private WeightModel  model;

    private int     index;
    private boolean running;

    private float currentTime;
    private float postSynapticTime;

    public WeightMain() {
        initUi();
        initOffheap();
    }

    public void addWeight(float time, float weight) {
        plotter.addPoint(POTENTIAL_FUNCTION_NAME, weight, weight);
    }

    public void run() {
        running = true;
        postSynapticTime = 0.0f;
        while (running) {
            // Sleep has no effect on the calculations!
            sleep();
            currentTime += TIMER_TICK;
            if (rand(0.75f)) {
                calculateStimulus();
                postSynapticTime = currentTime;
            } else {
                calculateFeedback(currentTime - postSynapticTime);
            }
        }
    }

    public void calculateFeedback(float deltaT) {
        if (running) {
            float potential = WeightView.applyFeedback(index, deltaT);
            plotter.addPoint(POTENTIAL_FUNCTION_NAME, deltaT, potential);
        }
    }

    public void calculateStimulus() {
        if (running) {
            float potential = randStimulus();
            WeightView.applyStimulus(index, potential, currentTime);
            plotter.addPoint(STIMULUS_FUNCTION_NAME, currentTime, potential);
        }
    }

    private void initUi() {
        this.plotter = GraphPlotter.frame(0.75, 0.5, () -> close())
            .withXRange(0.0, 20.0, 20)
            .withYRange(-70, 70, 14)
            .addFunction(POTENTIAL_FUNCTION_NAME, Color.blue)
            //            .addFunction(STIMULUS_FUNCTION_NAME, Color.red)
            //            .addFunction(FEEDBACK_FUNCTION_NAME, Color.green)
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
        System.out.println("\ndone");
    }

    private static void sleep() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public float randStimulus() {
        return ThreadLocalRandom.current().nextFloat() * 70.0f - 50.f;
    }

    public boolean rand(float pct) {
        return ThreadLocalRandom.current().nextFloat() < pct;
    }

    public static void main(String[] args) {
        WeightMain main = new WeightMain();
        main.run();
    }

}
