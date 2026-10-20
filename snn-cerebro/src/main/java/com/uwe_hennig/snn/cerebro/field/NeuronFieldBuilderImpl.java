/// @(#)NeuronFieldBuilderImpl.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.field;

import java.util.ArrayList;
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

    private List<FieldNode> receptorFields = new ArrayList<>();
    private List<FieldNode> effectorFields = new ArrayList<>();
    private List<FieldNode> associativeFields = new ArrayList<>();
    private List<FieldNode> feedbackFields = new ArrayList<>();

    private GraphType receptorGraphType;
    private int       maxReceptorNeurons;
    private int       effectorAssociativeEdges;

    private GraphType effectorGraphType;
    private int       maxEffectorNeurons;

    private GraphType feedbackGraphType;
    private int       maxFeedbackNeurons;
    private int       groupEdges;

    private GraphType associativeGraphType;
    private int       maxAssociativeNeurons;
    private int       associativeEffectorEdges;

    @Override
    public NeuronFieldBuilder addReceptorFields(GraphType type, int effectorAssociativeEdges, int maxNeuronCount) {
        assert type != null : "invalid receptor type";
        assert maxNeuronCount >= 1 : "invalid receptor maxNeuronCount";
        assert effectorAssociativeEdges >= 1 : "invalid parameter effectorAssociativeEdges";

        this.receptorGraphType = type;
        this.maxReceptorNeurons = maxNeuronCount;
        this.effectorAssociativeEdges = effectorAssociativeEdges;

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
    public NeuronFieldBuilder addFeedbackFields(GraphType type, int groupEdges, int maxNeuronCount) {
        assert type != null : "invalid feedback type";
        assert maxNeuronCount >= 1 : "invalid feedback maxNeuronCount";
        assert groupEdges >= 1 : "invalid parameter groupEdges";

        this.feedbackGraphType = type;
        this.maxFeedbackNeurons = maxNeuronCount;
        this.groupEdges = groupEdges;

        return this;
    }

    @Override
    public NeuronFieldBuilder addAssociativeFields(GraphType type, int associativeEffectorEdges, int maxNeuronCount) {
        assert type != null : "invalid associative type";
        assert maxNeuronCount >= 1 : "invalid associative maxNeuronCount";
        assert associativeEffectorEdges >=1 : "invalid parameter associativeEffectorEdges";

        this.associativeGraphType = type;
        this.maxAssociativeNeurons = maxNeuronCount;
        this.associativeEffectorEdges = associativeEffectorEdges;

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
    public String toDot(String comment) {
        StringBuilder builder = new StringBuilder();
        builder.append("digraph SNN {\n");
        builder.append("// " + comment + "\n");
        builder.append(" compound=true;\n");
        builder.append(" node [shape=none];\n");
        builder.append(" edge [arrowhead=empty, color=red];\n");
        builder.append(" layout = dot;\n");

        builder.append(" subgraph cluster_receptor {\n");
        builder.append("\t label = \"receptor\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_receptor [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (int i = 0; i < receptorFields.size(); i++) {
            builder.append(receptorFields.get(i).id + ";\n");
        }

        builder.append(" }\n");

        builder.append(" subgraph cluster_associative {\n");
        builder.append("\t label = \"associative\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_associative [shape=point, style=invis, label=\"\", width=0, height=0];\n");


        for (int i = 0; i < associativeFields.size(); i++) {
            builder.append("\t" + associativeFields.get(i).id + ";\n");
        }
        builder.append(" }\n");

        builder.append(" subgraph cluster_feedback {\n");
        builder.append("\t label = \"feedback\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_feedback [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (int i = 0; i < associativeFields.size(); i++) {
            builder.append("\t" + feedbackFields.get(i).id + ";\n");
        }
        builder.append(" }\n");

        builder.append(" subgraph cluster_effector {\n");
        builder.append("\t label = \"effector\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_effector [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (int i = 0; i < effectorFields.size(); i++) {
            builder.append("\t " + effectorFields.get(i).id + ";\n");
        }
        builder.append(" }\n");

        // connect subgraphs
        builder.append(" anchor_receptor:s    -> anchor_associative:n [ltail=cluster_receptor, lhead=cluster_associative, color=blue];\n");
        builder.append(" anchor_associative:s ->  anchor_effector:n   [ltail=cluster_associative, lhead=cluster_effector, color=blue];\n");
        builder.append(" anchor_feedback:s    -> anchor_receptor:n    [ltail=cluster_feedback, lhead=cluster_receptor, color=blue];\n");
        builder.append(" anchor_feedback:s    -> anchor_associative:n [ltail=cluster_feedback, lhead=cluster_associative, color=blue];\n");
        builder.append(" anchor_feedback:s    -> anchor_effector:n    [ltail=cluster_feedback, lhead=cluster_effector, color=blue];\n");

        // connections
        // TODO

        builder.append("}\n");

        return builder.toString();
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

    public static void main(String[] args) {
        NeuronFieldBuilderImpl nb = new NeuronFieldBuilderImpl();
        System.out.println(nb.toDot("test"));
    }
}
