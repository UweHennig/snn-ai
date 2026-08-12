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

import com.uwe_hennig.snn.analytics.ui.DotFileParser.Node;

/**
 * DrawingPanel
 *
 * @author Uwe Hennig
 */
public class GraphCanvas extends JPanel {
    private static final long serialVersionUID = 1L;
    private final int         NODE_RADIUS      = 5;

    private List<Node> nodes = new ArrayList<>();

    private File file;

    public GraphCanvas() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createTitledBorder("Painting area"));
    }

    public void setFile(File file) {
        try {
            this.file = file;
            nodes = DotFileParser.parse(file.getAbsolutePath());

            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.5f));

        for (DotFileParser.Node source : nodes) {
            for (DotFileParser.Node target : source.neighbors) {
                drawArrow(g2d, (int)source.x, (int)source.y, (int)target.x, (int)target.y);
            }
        }

        for (DotFileParser.Node node : nodes) {
            int x = (int) node.x - NODE_RADIUS;
            int y = (int) node.y - NODE_RADIUS;

            g2d.setColor(Color.WHITE);
            g2d.fillOval(x, y, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

            g2d.setColor(Color.BLUE);
            g2d.drawOval(x, y, 2 * NODE_RADIUS, 2 * NODE_RADIUS);

            g2d.setColor(Color.BLACK);
            String label = String.valueOf(node.id);
            FontMetrics fm = g2d.getFontMetrics();
            int labelX = (int) node.x - (fm.stringWidth(label) / 2);
            int labelY = (int) node.y + (fm.getAscent() / 2) - 2;
            g2d.drawString(label, labelX, labelY);
        }
    }

    private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
        int arrowSize = 10;
        double dx = x2 - x1;
        double dy = y2 - y1;
        double angle = Math.atan2(dy, dx);

        g2.drawLine(x1, y1, x2, y2);

        double offset = NODE_RADIUS + 2;
        int ox = (int) (x2 - offset * Math.cos(angle));
        int oy = (int) (y2 - offset * Math.sin(angle));

        AffineTransform tx = new AffineTransform();
        tx.setToIdentity();
        tx.translate(ox, oy);
        tx.rotate(angle - Math.PI / 2d);

        Path2D arrowHead = new Path2D.Double();
        arrowHead.moveTo(0, 0);
        arrowHead.lineTo(-5, -arrowSize);
        arrowHead.lineTo(5, -arrowSize);
        arrowHead.closePath();

        g2.fill(tx.createTransformedShape(arrowHead));
    }
}
