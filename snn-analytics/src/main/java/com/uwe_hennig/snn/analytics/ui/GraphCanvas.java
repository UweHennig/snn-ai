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

/**
 * DrawingPanel
 *
 * @author Uwe Hennig
 */
public class GraphCanvas extends JPanel {
    private static final long serialVersionUID = 1L;
    private final int         NODE_RADIUS      = 15; // Etwas größer für bessere Sichtbarkeit
    private final int         PADDING          = 50; // Abstand zum Rand

    private List<DotFileParser.Node> nodes = new ArrayList<>();

    public GraphCanvas() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Painting area"));
    }

    public void setFile(File file) {
        try {
            nodes = DotFileParser.parse(file.getAbsolutePath());
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
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

        // 1. Extremwerte finden (Bounding Box)
        double minX = Double.MAX_VALUE, maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE, maxY = -Double.MAX_VALUE;

        for (DotFileParser.Node n : nodes) {
            minX = Math.min(minX, n.x);
            maxX = Math.max(maxX, n.x);
            minY = Math.min(minY, n.y);
            maxY = Math.max(maxY, n.y);
        }

        // Breite und Höhe des Graphen berechnen
        double graphWidth = maxX - minX;
        double graphHeight = maxY - minY;
        if (graphWidth == 0) {
            graphWidth = 1; // Division durch Null verhindern
        }
        if (graphHeight == 0) {
            graphHeight = 1;
        }

        // 2. Skalierung berechnen (Fit to Screen)
        double scaleX = (getWidth() - 2 * PADDING) / graphWidth;
        double scaleY = (getHeight() - 2 * PADDING) / graphHeight;
        double scale = Math.min(scaleX, scaleY); // Proportionen erhalten

        // 3. Offsets für die Zentrierung berechnen
        double offsetX = (getWidth() - graphWidth * scale) / 2 - minX * scale;
        double offsetY = (getHeight() - graphHeight * scale) / 2 - minY * scale;

        // Hilfsfunktion zur Koordinatenumrechnung
        // Wir transformieren die "Welt-Koordinaten" (0-100) in "Screen-Koordinaten"
        java.util.function.IntUnaryOperator tx = (x) -> (int) (x * scale + offsetX);
        java.util.function.IntUnaryOperator ty = (y) -> (int) (y * scale + offsetY);

        // 4. Kanten zeichnen
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));
        for (DotFileParser.Node source : nodes) {
            for (DotFileParser.Node target : source.neighbors) {
                drawArrow(g2d, tx.applyAsInt((int) source.x), ty.applyAsInt((int) source.y), tx.applyAsInt((int) target.x), ty.applyAsInt((int) target.y));
            }
        }

        // 5. Knoten zeichnen
        for (DotFileParser.Node node : nodes) {
            int x = tx.applyAsInt((int) node.x);
            int y = ty.applyAsInt((int) node.y);

            // Kreis
            g2d.setColor(Color.WHITE);
            g2d.fillOval(x - NODE_RADIUS, y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);
            g2d.setColor(Color.BLUE);
            g2d.drawOval(x - NODE_RADIUS, y - NODE_RADIUS, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

            // Label
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

        // Pfeilspitze am Rand des Kreises stoppen lassen
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