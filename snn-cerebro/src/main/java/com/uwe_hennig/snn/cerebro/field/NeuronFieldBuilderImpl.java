/// @(#)NeuronFieldBuilderImpl.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.field;

import static com.uwe_hennig.snn.contracts.core.NeuronFieldType.ASSOCIATIVE_FIELD;
import static com.uwe_hennig.snn.contracts.core.NeuronFieldType.EFFECTOR_FIELD;
import static com.uwe_hennig.snn.contracts.core.NeuronFieldType.FEEDBACK_FIELD;
import static com.uwe_hennig.snn.contracts.core.NeuronFieldType.RECEPTOR_FIELD;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.uwe_hennig.snn.cerebro.contracts.FieldNetwork;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;

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

    private int countReceptorFields;
    private int maxReceptorNeurons;
    private int countEffectorFields;
    private int maxEffectorNeurons;
    private int countFeedbackFields;
    private int maxFeedbackNeurons;
    private int countAssociativeFields;
    private int maxAssociativeNeurons;

    private int receptorOutDegree;
    private int effectorInDegree;
    private int associativeInDegree;
    private int associativeOutDegree;
    private int feedbackInDegree;
    private int feedbackOutDegree;

    private int             fieldId = 0;
    private List<FieldNode> receptorFields;
    private List<FieldNode> effectorFields;
    private List<FieldNode> associativeFields;
    private List<FieldNode> feedbackFields;

    @Override
    public NeuronFieldBuilder addReceptorFields(int countFields, int maxNeuronCount) {
        assert countFields >= 1 : "invalid receptor countFields";
        assert maxNeuronCount >= 1 : "invalid receptor maxNeuronCount";

        this.countReceptorFields = countFields;
        this.maxReceptorNeurons = maxNeuronCount;

        return this;
    }

    @Override
    public NeuronFieldBuilder addEffectorFields(int countFields, int maxNeuronCount) {
        assert countFields >= 1 : "invalid effector countFields";
        assert maxNeuronCount >= 1 : "invalid effector maxNeuronCount";

        this.countEffectorFields = countFields;
        this.maxEffectorNeurons = maxNeuronCount;

        return this;
    }

    @Override
    public NeuronFieldBuilder addFeedbackFields(int countFields, int maxNeuronCount) {
        assert countFields >= 1 : "invalid feedback countFields";
        assert maxNeuronCount >= 1 : "invalid feedback maxNeuronCount";

        this.countFeedbackFields = countFields;
        this.maxFeedbackNeurons = maxNeuronCount;

        return this;
    }

    @Override
    public NeuronFieldBuilder addAssociativeFields(int countFields, int maxNeuronCount) {
        assert countFields >= 1 : "invalid associative countFields";
        assert maxNeuronCount >= 1 : "invalid associative maxNeuronCount";

        this.countAssociativeFields = countFields;
        this.maxAssociativeNeurons = maxNeuronCount;

        return this;
    }

    // --- Wire methods ---

    @Override
    public NeuronFieldBuilder wireReceptors(int outDegree) {
        assert outDegree >= 1 : "invalid receptor out degree";
        this.receptorOutDegree = outDegree;

        receptorFields = new ArrayList<>(countReceptorFields);
        for (int i = 0; i < countReceptorFields; i++) {
            receptorFields.add(new FieldNode(RECEPTOR_FIELD, fieldId++, EMPTY_ARRAY, new int[outDegree], maxReceptorNeurons));
        }

        return this;
    }

    @Override
    public NeuronFieldBuilder wireEffectors(int inDegree) {
        assert inDegree >= 1 : "invalid effector in degree";
        this.effectorInDegree = inDegree;

        effectorFields = new ArrayList<>(countEffectorFields);
        for (int i = 0; i < countEffectorFields; i++) {
            effectorFields.add(new FieldNode(EFFECTOR_FIELD, fieldId++, new int[inDegree], EMPTY_ARRAY, maxEffectorNeurons));
        }

        return this;
    }

    @Override
    public NeuronFieldBuilder wireAssociative(int inDegree, int outDegree) {
        assert inDegree >= 1 : "invalid associative in degree";
        assert outDegree >= 1 : "invalid associative out degree";

        this.associativeInDegree = inDegree;
        this.associativeOutDegree = outDegree;

        associativeFields = new ArrayList<>(countAssociativeFields);
        for (int i = 0; i < countAssociativeFields; i++) {
            associativeFields.add(new FieldNode(ASSOCIATIVE_FIELD, fieldId++, new int[inDegree], new int[outDegree], maxAssociativeNeurons));
        }

        return this;
    }

    @Override
    public NeuronFieldBuilder wireFeedback(int inDegree, int outDegree) {
        assert inDegree >= 1 : "invalid feedback in degree";
        assert outDegree >= 1 : "invalid feedback out degree";

        this.feedbackInDegree = inDegree;
        this.feedbackOutDegree = outDegree;

        feedbackFields = new ArrayList<>(countFeedbackFields);
        for (int i = 0; i < countFeedbackFields; i++) {
            feedbackFields.add(new FieldNode(FEEDBACK_FIELD, fieldId++, new int[inDegree], new int[outDegree], maxFeedbackNeurons));
        }

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
        builder.append("\t// receptor fields\n");
        for (int i = 0; i < receptorFields.size(); i++) {
            for (int j = 0; j < receptorOutDegree; j++) {
                builder.append("\t" + receptorFields.get(i).id + "->" + receptorFields.get(i).outNeighbours[j] + ";\n");
            }
        }

        builder.append("\t// associative fields\n");
        for (int i = 0; i < associativeFields.size(); i++) {
            for (int j = 0; j < associativeOutDegree; j++) {
                builder.append("\t" + associativeFields.get(i).id + "->" + associativeFields.get(i).outNeighbours[j] + ";\n");
            }
        }

        builder.append("\t// feedback fields\n");
        for (int i = 0; i < feedbackFields.size(); i++) {
            for (int j = 0; j < feedbackOutDegree; j++) {
                builder.append("\t" + feedbackFields.get(i).id + "->" + feedbackFields.get(i).outNeighbours[j] + ";\n");
            }
        }

        builder.append("\t// no out effector fields\n");

        builder.append("}\n");
    }

    @Override
    public long totalBytes() {
        int totalFields = countReceptorFields + countEffectorFields + countFeedbackFields + countAssociativeFields;
        int totalNeurons = maxReceptorNeurons * countReceptorFields + maxEffectorNeurons * countEffectorFields + maxFeedbackNeurons * countFeedbackFields
            + maxAssociativeNeurons * countAssociativeFields;

        int totalDegrees = receptorOutDegree * countReceptorFields + effectorInDegree * countEffectorFields + associativeInDegree * countAssociativeFields
            + associativeOutDegree * countAssociativeFields + feedbackInDegree * countFeedbackFields + feedbackOutDegree * countFeedbackFields;

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
        return countReceptorFields >= 1 && maxReceptorNeurons >= 1 && countEffectorFields >= 1 && maxEffectorNeurons >= 1 && countFeedbackFields >= 1
            && maxFeedbackNeurons >= 1 && countAssociativeFields >= 1 && maxAssociativeNeurons >= 1 && receptorOutDegree >= 1 && effectorInDegree >= 1
            && associativeInDegree >= 1 && associativeOutDegree >= 1 && feedbackInDegree >= 1 && feedbackOutDegree >= 1;
    }
}
