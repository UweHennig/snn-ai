/**
 * @(#)DrawingPanel.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.analytics.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * DrawingPanel
 *
 * @author Uwe Hennig
 */
public class GraphCanvas extends JPanel {
    private static final long serialVersionUID = 1L;
    private final int         NODE_RADIUS      = 15;
    private final int         PADDING          = 50;

    private Timer simulationTimer;

    private List<DotFileParser.Node> nodes = new ArrayList<>();

    public GraphCanvas() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Painting area"));

        simulationTimer = new Timer(30, _ -> {
            simulateForces();
            repaint();
        });
    }

    public void setFile(File file) {
        try {
            nodes = DotFileParser.parse(file.getAbsolutePath());
            simulationTimer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void simulateForces() {
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        // Stability parameters
        double repulsionConstant = 1500.0;
        double springConstant = 0.1;
        double damping = 0.7;
        double centerGravity = 0.02;
        double maxVelocity = 10.0;

        double totalMovement = 0;

        // 1. Repulsion
        for (int i = 0; i < nodes.size(); i++) {
            DotFileParser.Node v = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                DotFileParser.Node u = nodes.get(j);

                double dx = v.x - u.x;
                double dy = v.y - u.y;
                double distSq = dx * dx + dy * dy + 1.0;
                double dist = Math.sqrt(distSq);

                double force = repulsionConstant / distSq;
                double fx = (dx / dist) * force;
                double fy = (dy / dist) * force;

                // Aktion = Reaktion (Symmetrie!)
                v.vx += fx;
                v.vy += fy;
                u.vx -= fx;
                u.vy -= fy;
            }
        }

        // 2.Attraction
        for (DotFileParser.Node v : nodes) {
            for (DotFileParser.Node neighbor : v.neighbors) {
                double dx = neighbor.x - v.x;
                double dy = neighbor.y - v.y;
                double dist = Math.sqrt(dx * dx + dy * dy) + 0.1;

                double force = springConstant * dist;
                double fx = (dx / dist) * force;
                double fy = (dy / dist) * force;

                v.vx += fx;
                v.vy += fy;
                neighbor.vx -= fx;
                neighbor.vy -= fy;
            }
        }

        // 3. Gravitation
        for (DotFileParser.Node n : nodes) {
            n.vx -= n.x * centerGravity;
            n.vy -= n.y * centerGravity;

            n.vx *= damping;
            n.vy *= damping;

            // Speed Limit
            double speed = Math.sqrt(n.vx * n.vx + n.vy * n.vy);
            if (speed > maxVelocity) {
                n.vx = (n.vx / speed) * maxVelocity;
                n.vy = (n.vy / speed) * maxVelocity;
            }

            n.x += n.vx;
            n.y += n.vy;
            totalMovement += speed;
        }

        // 4. Stop condition
        if (totalMovement < nodes.size() * 0.01) {
            simulationTimer.stop();
            for (DotFileParser.Node n : nodes) {
                n.vx = 0;
                n.vy = 0;
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (nodes == null || nodes.isEmpty()) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 1. Bounding Box
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (DotFileParser.Node n : nodes) {
            minX = Math.min(minX, n.x);
            maxX = Math.max(maxX, n.x);
            minY = Math.min(minY, n.y);
            maxY = Math.max(maxY, n.y);
        }

        double graphWidth = maxX - minX;
        double graphHeight = maxY - minY;
        if (graphWidth == 0) {
            graphWidth = 1;
        }
        if (graphHeight == 0) {
            graphHeight = 1;
        }

        // 2. Fit to Screen
        double scaleX = (getWidth() - 2 * PADDING) / graphWidth;
        double scaleY = (getHeight() - 2 * PADDING) / graphHeight;
        double scale = Math.min(scaleX, scaleY);

        // 3. Offsets
        double offsetX = (getWidth() - graphWidth * scale) / 2 - minX * scale;
        double offsetY = (getHeight() - graphHeight * scale) / 2 - minY * scale;

        // Convert screen coordinates
        java.util.function.IntUnaryOperator tx = (x) -> (int) (x * scale + offsetX);
        java.util.function.IntUnaryOperator ty = (y) -> (int) (y * scale + offsetY);

        // 4. Paint edges
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        for (DotFileParser.Node source : nodes) {
            for (DotFileParser.Node target : source.neighbors) {
                drawArrow(g2d, tx.applyAsInt((int) source.x), ty.applyAsInt((int) source.y), tx.applyAsInt((int) target.x), ty.applyAsInt((int) target.y));
            }
        }

        // 5. Paint Nodes
        for (DotFileParser.Node node : nodes) {
            int x = tx.applyAsInt((int) node.x);
            int y = ty.applyAsInt((int) node.y);

            g2d.setColor(Color.WHITE);
            g2d.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);
            g2d.setColor(Color.BLUE);
            g2d.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

            g2d.setColor(Color.BLACK);
            String label = String.valueOf(node.id);
            FontMetrics fm = g2d.getFontMetrics();
            int labelX = x - (fm.stringWidth(label) / 2);
            int labelY = y + (fm.getAscent() / 2) - 2;
            g2d.drawString(label, labelX, labelY);
        }
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        int arrowSize = 8;
        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dy, dx);

        g2.drawLine(x1, y1, x2, y2);

        double offset = NODE_RADIUS + 1;
        int ox = (int) (x2 - offset * Math.cos(angle));
        int oy = (int) (y2 - offset * Math.sin(angle));

        AffineTransform tx = new AffineTransform();
        tx.translate(ox, oy);
        tx.rotate(angle - Math.PI / 2d);

        Path2D arrowHead = new Path2D.Double();
        arrowHead.moveTo(0, 0);
        arrowHead.lineTo(-4, -arrowSize);
        arrowHead.lineTo(4, -arrowSize);
        arrowHead.closePath();

        g2.fill(tx.createTransformedShape(arrowHead));
    }
}