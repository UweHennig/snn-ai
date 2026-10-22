/// @(#)NeuronFieldBuilderImpl.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.field;

import static com.uwe_hennig.snn.contracts.core.NeuronFieldType.ASSOCIATIVE_FIELD;
import static com.uwe_hennig.snn.contracts.core.NeuronFieldType.EFFECTOR_FIELD;
import static com.uwe_hennig.snn.contracts.core.NeuronFieldType.FEEDBACK_FIELD;
import static com.uwe_hennig.snn.contracts.core.NeuronFieldType.RECEPTOR_FIELD;

import java.util.LinkedHashSet;
import java.util.Set;

import com.uwe_hennig.snn.cerebro.contracts.FieldNetwork;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;
import com.uwe_hennig.snn.contracts.graph.GraphType;

/// NeuronFieldBuilderImpl
///
/// @author Uwe Hennig
///
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder, FieldNetwork {
    private static final int[] EMPTY_ARRAY = new int[0];

    private final record FieldNode(int fieldType, int id, Set<Integer> inNeighbours, Set<Integer> outNeighbours, int maxNeurons) {
        public FieldNode addInNeighbour(int id) {
            inNeighbours.add(id);
            return this;
        }

        public FieldNode addOutNeighbour(int id) {
            outNeighbours.add(id);
            return this;
        }
    }

    private Set<FieldNode> receptorFields    = new LinkedHashSet<>();
    private Set<FieldNode> effectorFields    = new LinkedHashSet<>();
    private Set<FieldNode> associativeFields = new LinkedHashSet<>();
    private Set<FieldNode> feedbackFields    = new LinkedHashSet<>();

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

    private int fieldId = 0;

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
        assert associativeEffectorEdges >= 1 : "invalid parameter associativeEffectorEdges";

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

        receptorFields = switch (receptorGraphType) {
            case GraphType.Random r -> random(RECEPTOR_FIELD, maxReceptorNeurons, r);
            case GraphType.Ring r -> ring(RECEPTOR_FIELD, maxReceptorNeurons, r);

            case GraphType.Fractal f -> fractal(RECEPTOR_FIELD, f);
            case GraphType.ScaleFree sf -> scaleFree(RECEPTOR_FIELD, sf);
            case GraphType.SmallWorld sw -> smallWorld(RECEPTOR_FIELD, sw);
            case GraphType.Tube t -> tube(RECEPTOR_FIELD, t);
            case GraphType.LayeredGrid l -> layeredGrid(RECEPTOR_FIELD, l);
            default -> throw new IllegalArgumentException("Unexpected value: " + receptorGraphType);
        };

        associativeFields = switch (associativeGraphType) {
            case GraphType.Random r -> random(ASSOCIATIVE_FIELD, maxAssociativeNeurons, r);
            case GraphType.Ring r -> ring(ASSOCIATIVE_FIELD, maxAssociativeNeurons, r);

            case GraphType.Fractal f -> fractal(ASSOCIATIVE_FIELD, f);
            case GraphType.ScaleFree sf -> scaleFree(ASSOCIATIVE_FIELD, sf);
            case GraphType.SmallWorld sw -> smallWorld(ASSOCIATIVE_FIELD, sw);
            case GraphType.Tube t -> tube(ASSOCIATIVE_FIELD, t);
            case GraphType.LayeredGrid l -> layeredGrid(ASSOCIATIVE_FIELD, l);
            default -> throw new IllegalArgumentException("Unexpected value: " + associativeGraphType);
        };

        effectorFields = switch (effectorGraphType) {
            case GraphType.Random r -> random(EFFECTOR_FIELD, maxEffectorNeurons, r);
            case GraphType.Ring r -> ring(EFFECTOR_FIELD, maxEffectorNeurons, r);

            case GraphType.Fractal f -> fractal(EFFECTOR_FIELD, f);
            case GraphType.ScaleFree sf -> scaleFree(EFFECTOR_FIELD, sf);
            case GraphType.SmallWorld sw -> smallWorld(EFFECTOR_FIELD, sw);
            case GraphType.Tube t -> tube(EFFECTOR_FIELD, t);
            case GraphType.LayeredGrid l -> layeredGrid(EFFECTOR_FIELD, l);
            default -> throw new IllegalArgumentException("Unexpected value: " + effectorGraphType);
        };

        feedbackFields = switch (feedbackGraphType) {
            case GraphType.Random r -> random(FEEDBACK_FIELD, maxFeedbackNeurons, r);
            case GraphType.Ring r -> ring(FEEDBACK_FIELD, maxFeedbackNeurons, r);

            case GraphType.Fractal f -> fractal(FEEDBACK_FIELD, f);
            case GraphType.ScaleFree sf -> scaleFree(FEEDBACK_FIELD, sf);
            case GraphType.SmallWorld sw -> smallWorld(FEEDBACK_FIELD, sw);
            case GraphType.Tube t -> tube(FEEDBACK_FIELD, t);
            case GraphType.LayeredGrid l -> layeredGrid(FEEDBACK_FIELD, l);
            default -> throw new IllegalArgumentException("Unexpected value: " + feedbackGraphType);
        };

        // TODO create connections between fields groups

        return this;
    }

    private Set<FieldNode> random(int fieldType, int maxNeurons, GraphType.Random randomGraph) {
        System.out.println("fields = " + randomGraph.nodes());
        Set<FieldNode> result = new LinkedHashSet<>();

        FieldNode currentNode = null;
        FieldNode previousNode = null;

        for (int i = 0; i < randomGraph.nodes(); i++) {
            currentNode = new FieldNode(fieldType, fieldId++, new LinkedHashSet<>(), new LinkedHashSet<>(), maxNeurons);
            result.add(currentNode);
            if (previousNode != null) {
                previousNode.addOutNeighbour(currentNode.id());
                currentNode.addInNeighbour(previousNode.id());
            }
            previousNode = currentNode;
        }

        if (previousNode != null) {
            currentNode.addInNeighbour(previousNode.id());
        }

        return result;
    }

    private Set<FieldNode> ring(int fieldType, int maxNeurons, GraphType.Ring ringGraph) {
        System.out.println("nodes = " + ringGraph.nodes());
        Set<FieldNode> result = new LinkedHashSet<>();

        FieldNode firstNode = null;
        FieldNode currentNode = null;
        FieldNode previousNode = null;

        for (int i = 0; i < ringGraph.nodes(); i++) {
            currentNode = new FieldNode(fieldType, fieldId++, new LinkedHashSet<>(), new LinkedHashSet<>(), maxNeurons);
            if (i == 0) {
                firstNode = currentNode;
            }
            result.add(currentNode);
            if (previousNode != null) {
                previousNode.addOutNeighbour(currentNode.id());
                currentNode.addInNeighbour(previousNode.id());
            }
            previousNode = currentNode;
        }

        if (previousNode != null) {
            currentNode.addInNeighbour(previousNode.id());
        }

        currentNode.addOutNeighbour(firstNode.id());
        firstNode.addInNeighbour(currentNode.id());

        return result;
    }

    private Set<FieldNode> fractal(int fieldType, GraphType.Fractal fractalGraph) {
        // TODO
        System.out.println("nodes = " + fractalGraph.nodes() + "depth = " + fractalGraph.depth());
        return new LinkedHashSet<>();
    }

    private Set<FieldNode> scaleFree(int fieldType, GraphType.ScaleFree scaleFreeGraph) {
        // TODO
        System.out.println("nodes = " + scaleFreeGraph.nodes() + " edgesPerNewNode = " + scaleFreeGraph.edgesPerNewNode());
        Set<FieldNode> result = new LinkedHashSet<>();

        return result;
    }

    private Set<FieldNode> smallWorld(int fieldType, GraphType.SmallWorld smallWorldGraph) {
        // TODO
        System.out.println("nodes = " + smallWorldGraph.nodes() + "neighbours = " + smallWorldGraph.neighbours() + " rewireProbability = "
            + smallWorldGraph.rewireProbability());
        Set<FieldNode> result = new LinkedHashSet<>();
        return result;
    }

    private Set<FieldNode> tube(int fieldType, GraphType.Tube tubeGrpah) {
        // TODO
        System.out.println("ringNodes = " + tubeGrpah.nodes() + " depth = " + tubeGrpah.depth());
        Set<FieldNode> result = new LinkedHashSet<>();
        return result;
    }

    private Set<FieldNode> layeredGrid(int fieldType, GraphType.LayeredGrid layeredGridGraph) {
        // TODO
        System.out.println("width = " + layeredGridGraph.width() + " height = " + layeredGridGraph.height() + " layers = " + layeredGridGraph.layers());
        Set<FieldNode> result = new LinkedHashSet<>();
        return result;
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

        for (FieldNode node : receptorFields) {
            builder.append(node.id() + ";\n");
        }
        builder.append(" }\n");

        builder.append(" subgraph cluster_associative {\n");
        builder.append("\t label = \"associative\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_associative [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (FieldNode node : associativeFields) {
            builder.append(node.id() + ";\n");
        }
        builder.append(" }\n");

        builder.append(" subgraph cluster_feedback {\n");
        builder.append("\t label = \"feedback\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_feedback [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (FieldNode node : feedbackFields) {
            builder.append(node.id() + ";\n");
        }
        builder.append(" }\n");

        builder.append(" subgraph cluster_effector {\n");
        builder.append("\t label = \"effector\";\n");
        builder.append("\t style = filled;\n");
        builder.append("\t color = lightgrey;\n");
        builder.append("\t anchor_effector [shape=point, style=invis, label=\"\", width=0, height=0];\n");

        for (FieldNode node : effectorFields) {
            builder.append(node.id() + ";\n");
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
