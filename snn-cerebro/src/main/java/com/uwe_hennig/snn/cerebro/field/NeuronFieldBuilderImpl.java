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
import com.uwe_hennig.snn.graph.generator.contracts.IdGenerator;
import com.uwe_hennig.snn.graph.generator.contracts.Node;
import com.uwe_hennig.snn.graph.util.GraphvizPrinter;

/// NeuronFieldBuilderImpl
///
/// @author Uwe Hennig
///
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder, FieldNetwork {
    private Set<Node> receptorFields    = new LinkedHashSet<>();
    private Set<Node> effectorFields    = new LinkedHashSet<>();
    private Set<Node> associativeFields = new LinkedHashSet<>();
    private Set<Node> feedbackFields    = new LinkedHashSet<>();

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

    private IdGenerator idGen = IdGenerator.ID_GENERATOR;

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

    private Set<Node> random(int fieldType, int maxNeurons, GraphType.Random randomGraph) {
        System.out.println("fields = " + randomGraph.nodes());
        Set<Node> result = new LinkedHashSet<>();

        Node currentNode = null;
        Node previousNode = null;

        for (int i = 0; i < randomGraph.nodes(); i++) {
            currentNode = new Node(idGen.apply(), fieldType, new LinkedHashSet<>(), new LinkedHashSet<>());
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

    private Set<Node> ring(int fieldType, int maxNeurons, GraphType.Ring ringGraph) {
        System.out.println("nodes = " + ringGraph.nodes());
        Set<Node> result = new LinkedHashSet<>();

        Node firstNode = null;
        Node currentNode = null;
        Node previousNode = null;

        for (int i = 0; i < ringGraph.nodes(); i++) {
            currentNode = new Node(idGen.apply(), fieldType, new LinkedHashSet<>(), new LinkedHashSet<>());
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

    private Set<Node> fractal(int fieldType, GraphType.Fractal fractalGraph) {
        // TODO
        System.out.println("nodes = " + fractalGraph.nodes() + "depth = " + fractalGraph.depth());
        return new LinkedHashSet<>();
    }

    private Set<Node> scaleFree(int fieldType, GraphType.ScaleFree scaleFreeGraph) {
        // TODO
        System.out.println("nodes = " + scaleFreeGraph.nodes() + " edgesPerNewNode = " + scaleFreeGraph.edgesPerNewNode());
        Set<Node> result = new LinkedHashSet<>();

        return result;
    }

    private Set<Node> smallWorld(int fieldType, GraphType.SmallWorld smallWorldGraph) {
        // TODO
        System.out.println("nodes = " + smallWorldGraph.nodes() + "stepSizeControl  = " + smallWorldGraph.stepSizeControl () + " rewireProbability = "
            + smallWorldGraph.rewireProbability());
        Set<Node> result = new LinkedHashSet<>();
        return result;
    }

    private Set<Node> tube(int fieldType, GraphType.Tube tubeGrpah) {
        // TODO
        System.out.println("ringNodes = " + tubeGrpah.nodes() + " depth = " + tubeGrpah.depth());
        Set<Node> result = new LinkedHashSet<>();
        return result;
    }

    private Set<Node> layeredGrid(int fieldType, GraphType.LayeredGrid layeredGridGraph) {
        // TODO
        System.out.println("width = " + layeredGridGraph.width() + " height = " + layeredGridGraph.height() + " layers = " + layeredGridGraph.layers());
        Set<Node> result = new LinkedHashSet<>();
        return result;
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

    @Override
    public String toDot(String comment) {
        return GraphvizPrinter.toDot(comment, receptorFields, effectorFields, associativeFields, feedbackFields);
    }
}
