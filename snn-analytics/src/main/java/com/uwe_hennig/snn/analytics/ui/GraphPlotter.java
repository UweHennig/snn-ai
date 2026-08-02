/**
 * @(#)GraphPlotter.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.analytics.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * GraphPlotter
 *
 * @author Uwe Hennig
 */
public class GraphPlotter extends JFrame {
    private JPanel                placeholder = new JPanel();
    private Map<String, Function> functionMap = new HashMap<>();

    private double xFrom  = 0.0;
    private double xUntil = 1.0;
    private double yFrom  = 0.0;
    private double yUntil = 1.0;

    private int xGrids = 10;
    private int yGrids = 10;

    private int paddingLeft   = 30;
    private int paddingRight  = 20;
    private int paddingTop    = 20;
    private int paddingBottom = 60;

    private double highYWater = Double.MIN_VALUE;
    private double lowYWater  = Double.MAX_VALUE;

    private double highXWater = Double.MIN_VALUE;
    private double lowXWater  = Double.MAX_VALUE;

    private record Point(double x, double y) {
    }

    private record Function(String name, Color color, List<Point> points) {
    }

    private GraphPlotter(int width, int height) {
        setSize(width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().add(placeholder);
    }

    public static GraphPlotter frame(int width, int height) {
        return new GraphPlotter(width, height);
    }

    public GraphPlotter withXRange(double xFrom, double xUntil, int grids) {
        this.xFrom = xFrom;
        this.xUntil = xUntil;
        this.xGrids = grids;
        return this;
    }

    public GraphPlotter withYRange(double yFrom, double yUntil, int grids) {
        this.yFrom = yFrom;
        this.yUntil = yUntil;
        this.yGrids = grids;
        return this;
    }

    public GraphPlotter addFunction(String name, Color color) {
        functionMap.put(name, new Function(name, color, new ArrayList<>()));
        return this;
    }

    public void addPoint(String name, double x, double y) {
        Function f = functionMap.get(name);
        if (f == null) {
            return;
        }

        List<Point> pts = f.points();
        pts.add(new Point(x, y));

        if (x > xUntil) {
            double shift = x - xUntil;
            xFrom += shift;
            xUntil += shift;

            for (Function fn : functionMap.values()) {
                List<Point> p = fn.points();
                while (!p.isEmpty() && p.get(0).x() < xFrom) {
                    p.remove(0);
                }
            }
        }

        lowYWater = Math.min(Math.min(lowYWater, y), y);
        highYWater = Math.max(Math.max(highYWater, y), y);

        lowXWater = Math.min(Math.min(lowXWater, x), x);
        highXWater = Math.max(Math.max(highXWater, x), x);

        repaint();
    }

    public GraphPlotter build() {
        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintGraph(g);
            }
        };

        getContentPane().remove(placeholder);
        getContentPane().add(canvas);

        setLocationRelativeTo(null);
        setVisible(true);

        revalidate();
        repaint();

        return this;
    }

    private void paintGraph(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawAxes(g2);
        drawFunctions(g2);
    }

    private void drawAxes(Graphics g2) {
        int xLeft = paddingLeft;
        int xRight = getWidth() - paddingRight;
        int yTop = paddingTop;
        int yBottom = getHeight() - paddingBottom;

        g2.setColor(Color.BLACK);
        g2.drawLine(xLeft, yBottom, xRight, yBottom); // X-Achse
        g2.drawLine(xLeft, yBottom, xLeft, yTop); // Y-Achse

        if (xUntil - xFrom == 0.0) {
            return;
        }
        if (yUntil - yFrom == 0.0) {
            return;
        }

        double xStep = (xUntil - xFrom) / xGrids;
        double yStep = (yUntil - yFrom) / yGrids;

        int tickSize = 6; // Length of the small strokes
        for (int i = 0; i <= xGrids; i++) {
            double xVal = xFrom + i * xStep;
            int px = toPixelX(xVal, xLeft, xRight);
            g2.drawLine(px, yBottom, px, yBottom - tickSize);

            // String label = String.format("%.2f", xVal);
            // g2.drawString(label, px - 10, yBottom + 15);
        }

        for (int i = 0; i <= yGrids; i++) {
            double yVal = yFrom + i * yStep;
            int py = toPixelY(yVal, yTop, yBottom);
            g2.drawLine(xLeft, py, xLeft + tickSize, py);

            String label = String.format("%.2f", yVal);
            g2.drawString(label, xLeft - 25, py + 5);
        }
    }

    private void drawFunctions(Graphics2D g2) {
        int xLeft = paddingLeft;
        int xRight = getWidth() - paddingRight;
        int yTop = paddingTop;
        int yBottom = getHeight() - paddingBottom;

        for (Function f : functionMap.values()) {
            List<Point> pts = f.points();
            g2.setColor(f.color());

            for (int i = 1; i < pts.size(); i++) {
                Point p1 = pts.get(i - 1);
                Point p2 = pts.get(i);

                int px1 = toPixelX(p1.x(), xLeft, xRight);
                int py1 = toPixelY(p1.y(), yTop, yBottom);

                int px2 = toPixelX(p2.x(), xLeft, xRight);
                int py2 = toPixelY(p2.y(), yTop, yBottom);

                g2.drawLine(px1, py1, px2, py2);

            }
            drawHiLoWatermark(g2);
        }
    }

    private void drawHiLoWatermark(Graphics2D g2) {
        int xLeft = paddingLeft;
        int xRight = getWidth() - paddingRight;
        int yTop = paddingTop;
        int yBottom = getHeight() - paddingBottom;

        g2.setColor(Color.RED);
        g2.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[] { 5, 5 }, 0));

        int yH = toPixelY(highYWater, yTop, yBottom);
        int yL = toPixelY(lowYWater, yTop, yBottom);
        g2.drawLine(xLeft, yH, xRight, yH);
        g2.drawLine(xLeft, yL, xRight, yL);

        g2.setColor(Color.BLACK);
        g2.drawString("High: " + String.format("%.2f", highYWater), xLeft + 20, yH - 5);
        g2.drawString("Low: " + String.format("%.2f", lowYWater), xLeft + 20, yL - 5);

        g2.drawString(String.format("X-Achse: [%2.1f .. %2.1f]", lowXWater, highXWater), xLeft + 20, yBottom + 20);
    }

    private int toPixelX(double x, int xLeft, int xRight) {
        double t = (x - xFrom) / (xUntil - xFrom);
        return (int) (xLeft + t * (xRight - xLeft));
    }

    private int toPixelY(double y, int yTop, int yBottom) {
        double t = (y - yFrom) / (yUntil - yFrom);
        return (int) (yBottom - t * (yBottom - yTop));
    }

    private static void sleep() {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        GraphPlotter plotter = GraphPlotter.frame(800, 600)
            .withXRange(0.0, 2.0 * Math.PI, 20)
            .withYRange(-1.1, 1.1, 10)
            .addFunction("sin", Color.blue)
            .addFunction("cos", Color.red)
            .addFunction("fun", Color.green)
            .build();

        for (double x = 0; x < 10.0 * Math.PI; x += 0.01) {
            plotter.addPoint("sin", x, Math.sin(x));
            plotter.addPoint("cos", x, Math.cos(4 * x));
            plotter.addPoint("fun", x, ((Math.random() - 0.5) / 2.0 + Math.sin(3.0 * x - 1.5)) / 2.0);
            sleep();
        }
    }

}
