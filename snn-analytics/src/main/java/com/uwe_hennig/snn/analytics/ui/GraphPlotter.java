/**
 * @(#)GraphPlotter.java
 * Copyright (c) 2026 Uwe Hennig
 * All rights reserved.
 */
package com.uwe_hennig.snn.analytics.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * GraphPlotter
 *
 * @author Uwe Hennig
 */
public class GraphPlotter extends JFrame {
    private static final long serialVersionUID = 1L;
    GraphCanvas               drawArea         = new GraphCanvas();

    public GraphPlotter() {
        setTitle("Graph Plotter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(Color.LIGHT_GRAY);
        controlPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 10));

        JButton fileBtn = new JButton("Select file...");
        JLabel fileLabel = new JLabel("No file selected");

        fileBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(GraphPlotter.this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    fileLabel.setText("File: " + selectedFile.getName());
                    if (drawArea != null) {
                        drawArea.setFile(selectedFile);
                    }
                }
            }
        });

        //        JButton resetBtn = new JButton("Reset Layout");
        //        resetBtn.addActionListener(e -> {
        //            for(DotFileParser.Node n : drawArea.getNodes()) {
        //                n.x = (Math.random() - 0.5) * 50;
        //                n.y = (Math.random() - 0.5) * 50;
        //                n.vx = 0; n.vy = 0;
        //            }
        //            drawArea.startSimulation();
        //        });
        //        controlPanel.add(resetBtn);

        controlPanel.add(fileBtn);
        controlPanel.add(fileLabel);

        add(controlPanel, BorderLayout.NORTH);
        add(drawArea, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new GraphPlotter().setVisible(true);
        });
    }

}
