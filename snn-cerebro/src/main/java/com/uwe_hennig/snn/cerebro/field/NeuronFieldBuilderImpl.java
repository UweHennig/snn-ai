/// @(#)NeuronFieldBuilderImpl.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.field;

import java.nio.file.Path;
import java.util.List;

import com.uwe_hennig.snn.cerebro.contracts.FieldNetwork;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;
import com.uwe_hennig.snn.contracts.graph.GraphType;

/// NeuronFieldBuilderImpl
///
/// @author Uwe Hennig
///
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder, FieldNetwork {
    private static final int[] EMPTY_ARRAY = new int[0];

    private final record FieldNode(int type, int id, int[] inNeighbours, int[] outNeighbours, int maxNeurons) {
        public FieldNode addInNeighbour(int pos, int id) {
            assert pos < inNeighbours.length : "invalid position for inNeighbours";
            inNeighbours[pos] = id;
            return this;
        }

        public FieldNode addOutNeighbour(int pos, int id) {
            assert pos < outNeighbours.length : "invalid position for outNeighbours";
            outNeighbours[pos] = id;
            return this;
        }
    }

    private List<FieldNode> receptorFields;
    private List<FieldNode> effectorFields;
    private List<FieldNode> associativeFields;
    private List<FieldNode> feedbackFields;

    private GraphType receptorGraphType;
    private int       maxReceptorNeurons;

    private GraphType effectorGraphType;
    private int       maxEffectorNeurons;

    private GraphType feedbackGraphType;
    private int       maxFeedbackNeurons;

    private GraphType associativeGraphType;
    private int       maxAssociativeNeurons;

    @Override
    public NeuronFieldBuilder addReceptorFields(GraphType type, int maxNeuronCount) {
        assert type != null : "invalid receptor type";
        assert maxNeuronCount >= 1 : "invalid receptor maxNeuronCount";

        this.receptorGraphType = type;
        this.maxReceptorNeurons = maxNeuronCount;

        return this;
    }

    @Override
    public NeuronFieldBuilder addEffectorFields(GraphType type, int maxNeuronCount) {
        assert type != null : "invalid effector type";
        assert maxNeuronCount >= 1 : "invalid effector maxNeuronCount";

        this.effectorGraphType = type;
        this.maxEffectorNeurons = maxNeuronCount;

        return this;
    }

    @Override
    public NeuronFieldBuilder addFeedbackFields(GraphType type, int maxNeuronCount) {
        assert type != null : "invalid feedback type";
        assert maxNeuronCount >= 1 : "invalid feedback maxNeuronCount";

        this.feedbackGraphType = type;
        this.maxFeedbackNeurons = maxNeuronCount;

        return this;
    }

    @Override
    public NeuronFieldBuilder addAssociativeFields(GraphType type, int maxNeuronCount) {
        assert type != null : "invalid associative type";
        assert maxNeuronCount >= 1 : "invalid associative maxNeuronCount";

        this.associativeGraphType = type;
        this.maxAssociativeNeurons = maxNeuronCount;

        return this;
    }

    // --- FieldNetwork methods ---

    @Override
    public FieldNetwork compile() {
        if (!valid()) {
            throw new IllegalArgumentException("Invalid parameters in NeuronFieldBuilder!");
        }

        // TODO create in/out connections
        // receptorFields

        return this;
    }

    @Override
    public void toDot(Path path, String comment) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph SNN {\n");
        builder.append("// " + comment + "\n");
        builder.append("node [shape=none];\n");
        builder.append("layout = neato;\n");

        builder.append("subgraph receptor {\n");
        builder.append("label = \"receptor\";");
        builder.append("style = filled;");
        builder.append("color = lightgrey;");

        for (int i = 0; i < receptorFields.size(); i++) {
            builder.append(receptorFields.get(i).id + ";\n");
        }

        builder.append("}\n");

        builder.append("subgraph associative {\n");
        builder.append("label = \"associative\";");
        builder.append("style = filled;");
        builder.append("color = lightgrey;");

        for (int i = 0; i < associativeFields.size(); i++) {
            builder.append("\t" + associativeFields.get(i).id + ";\n");
        }
        builder.append("}\n");

        builder.append("subgraph feedback {\n");
        builder.append("label = \"feedback\";");
        builder.append("style = filled;");
        builder.append("color = lightgrey;");

        for (int i = 0; i < associativeFields.size(); i++) {
            builder.append("\t" + feedbackFields.get(i).id + ";\n");
        }
        builder.append("}\n");

        builder.append("subgraph effector {\n");
        builder.append("label = \"effector\";");
        builder.append("style = filled;");
        builder.append("color = lightgrey;");

        for (int i = 0; i < effectorFields.size(); i++) {
            builder.append("\t" + effectorFields.get(i).id + ";\n");
        }
        builder.append("}\n");

        // connections
        // TODO

        builder.append("}\n");
    }

    @Override
    public long totalBytes() {
        // TODO
        int totalFields = 0;
        int totalNeurons = 0;
        int totalDegrees = 0;

        int bytes = 32; // meta info
        bytes += totalFields * 36; // field header
        bytes += totalNeurons * 4; // neuron ids
        bytes += totalDegrees * 4; // in/out degree
        bytes += totalFields * 4; // currently 1 data per field

        return bytes;
    }

    @Override
    public NeuronField build() {
        // TODO Auto-generated method stub class FieldNetwork
        return null;
    }

    private boolean valid() {
        // TODO
        return false;
    }
}
