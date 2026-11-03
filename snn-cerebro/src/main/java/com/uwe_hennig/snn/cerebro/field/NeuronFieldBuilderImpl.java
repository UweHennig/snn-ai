/// @(#)NeuronFieldBuilderImpl.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.field;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.uwe_hennig.snn.anatomy.neuron.NeuronFieldModel;
import com.uwe_hennig.snn.cerebro.contracts.FieldCompileResult;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;
import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.graph.generator.contracts.IdGenerator;
import com.uwe_hennig.snn.graph.generator.contracts.Node;
import com.uwe_hennig.snn.graph.util.GraphvizPrinter;

/// NeuronFieldBuilderImpl Newman- Watts Version
///
/// @author Uwe Hennig
///
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder, FieldCompileResult {
    private List<Node> receptorFields    = new ArrayList<>();
    private List<Node> effectorFields    = new ArrayList<>();
    private List<Node> associativeFields = new ArrayList<>();
    private List<Node> feedbackFields    = new ArrayList<>();

    private List<String> errorMsg = new ArrayList<>();

    // --- NeuronFieldBuilder ---
    IdGenerator idGen = IdGenerator.ID_GENERATOR;

    private int receptorEdges;
    private int associativeEdges;
    private int effectorEdges;
    private int feedbackEdges;

    private float receptorProbability;
    private float associativeProbability;
    private float effectorProbability;
    private float feedbackProbability;

    private int receptorForwardConnections;
    private int associativeForwardConnections;
    private int associativeBackwardsConnections;
    private int effectorBackwardsConnections;
    private int feedbackForwardConnections;

    @Override
    public NeuronFieldBuilder withReceptorFields(int numFiedlds, int edges, float probability) {
        validParamters("receptor", numFiedlds, edges, probability);
        this.receptorEdges = even(edges);
        this.receptorProbability = probability;

        for (int i = 0; i < numFiedlds; i++) {
            receptorFields.add(new Node(idGen.apply(), NeuronFieldType.RECEPTOR_FIELD, new ArrayList<>()));
        }

        return this;
    }

    @Override
    public NeuronFieldBuilder withAssociativeFields(int numFiedlds, int edges, float probability) {
        validParamters("associative", numFiedlds, edges, probability);
        this.associativeEdges = even(edges);
        this.associativeProbability = probability;

        for (int i = 0; i < numFiedlds; i++) {
            associativeFields.add(new Node(idGen.apply(), NeuronFieldType.ASSOCIATIVE_FIELD, new ArrayList<>()));
        }
        return this;
    }

    @Override
    public NeuronFieldBuilder withEffectorFields(int numFiedlds, int edges, float probability) {
        validParamters("effector", numFiedlds, edges, probability);
        this.effectorEdges = even(edges);
        this.effectorProbability = probability;

        for (int i = 0; i < numFiedlds; i++) {
            effectorFields.add(new Node(idGen.apply(), NeuronFieldType.EFFECTOR_FIELD, new ArrayList<>()));
        }
        return this;
    }

    @Override
    public NeuronFieldBuilder withFeedbackFields(int numFiedlds, int edges, float probability) {
        validParamters("feedback", numFiedlds, edges, probability);
        this.feedbackEdges = even(edges);
        this.feedbackProbability = probability;

        for (int i = 0; i < numFiedlds; i++) {
            feedbackFields.add(new Node(idGen.apply(), NeuronFieldType.FEEDBACK_FIELD, new ArrayList<>()));
        }
        return this;
    }

    @Override
    public NeuronFieldBuilder connectReceptor(int forwardConnections) {
        if (forwardConnections < 1) {
            errorMsg.add("At least one receptor field connection is required");
        }
        this.receptorForwardConnections = forwardConnections;
        return this;
    }

    @Override
    public NeuronFieldBuilder connectAssociative(int fowardConnections, int backwardsConnections) {
        if (fowardConnections < 1 || backwardsConnections < 1) {
            errorMsg.add("Each direction requires at least one associative connection");
        }

        this.associativeForwardConnections = fowardConnections;
        this.associativeBackwardsConnections = backwardsConnections;
        return this;

    }

    @Override
    public NeuronFieldBuilder connectEffector(int backwardsConnections) {
        if (backwardsConnections < 1) {
            errorMsg.add("At least one effector field connection required");
        }
        this.effectorBackwardsConnections = backwardsConnections;
        return this;

    }

    @Override
    public NeuronFieldBuilder connectFeedback(int forwardConnections) {
        if (forwardConnections < 3) {
            errorMsg.add("At least three feedback connections are required");
        }
        this.feedbackForwardConnections = forwardConnections;

        return this;
    }

    @Override
    public FieldCompileResult compile() {
        validate();
        receptorFields = smallWorld(receptorFields, receptorEdges, receptorProbability);
        associativeFields = smallWorld(associativeFields, associativeEdges, associativeProbability);
        effectorFields = smallWorld(effectorFields, effectorEdges, effectorProbability);
        feedbackFields = smallWorld(feedbackFields, feedbackEdges, feedbackProbability);

        // TODO connections
        List<Node> allNodes = Stream.of(receptorFields, associativeFields, effectorFields, feedbackFields).flatMap(List::stream).collect(Collectors.toList());

        connect(receptorFields, List.of(), associativeFields, 0, receptorForwardConnections);
        connect(associativeFields, receptorFields, effectorFields, associativeBackwardsConnections, associativeForwardConnections);
        connect(effectorFields, associativeFields, List.of(), effectorBackwardsConnections, 0);
        connect(feedbackFields, List.of(), allNodes, 0, feedbackForwardConnections);

        return this;
    }

    private void connect(List<Node> srcNodes, List<Node> backwardsNodes, List<Node> forwardNodes, int numBackward, int numForward) {
        if (srcNodes == null || srcNodes.isEmpty()) {
            return;
        }

        Random rand = new Random(System.currentTimeMillis());
        connectToTargetLayer(srcNodes, backwardsNodes, numBackward, rand);
        connectToTargetLayer(srcNodes, forwardNodes, numForward, rand);
    }

    private void connectToTargetLayer(List<Node> srcNodes, List<Node> targetNodes, int requestedCount, Random rand) {
        if (targetNodes == null || targetNodes.isEmpty() || requestedCount <= 0) {
            return;
        }

        int maxPossible = Math.min(requestedCount, targetNodes.size());

        Map<Integer, Integer> usageCount = new HashMap<>();
        for (Node target : targetNodes) {
            usageCount.put(target.id(), target.neighbours().size());
        }

        for (Node src : srcNodes) {
            Set<Integer> existingNeighbours = new HashSet<>(src.neighbours());

            List<Node> candidates = new ArrayList<>();
            for (Node target : targetNodes) {
                if (!existingNeighbours.contains(target.id()) && target.id() != src.id()) {
                    candidates.add(target);
                }
            }

            int toPick = Math.min(maxPossible, candidates.size());
            if (toPick == 0) {
                continue;
            }

            Collections.shuffle(candidates, rand);
            candidates.sort(Comparator.comparingInt(node -> usageCount.getOrDefault(node.id(), 0)));

            for (int i = 0; i < toPick; i++) {
                Node chosen = candidates.get(i);

                src.addNeighbour(chosen.id());

                usageCount.put(chosen.id(), usageCount.getOrDefault(chosen.id(), 0) + 1);
            }
        }
    }

    private boolean validate() {
        boolean valid = true;
        if (receptorFields.isEmpty()) {
            errorMsg.add("n receptor field defined");
            valid = false;
        }
        if (associativeFields.isEmpty()) {
            errorMsg.add("no associative field defined");
            valid = false;
        }
        if (effectorFields.isEmpty()) {
            errorMsg.add("no effector field defined");
            valid = false;
        }
        if (feedbackFields.isEmpty()) {
            errorMsg.add("no feedback field defined");
            valid = false;
        }
        // TODO more checks
        return valid;
    }

    // --- FieldCompileResult ---

    @Override
    public String toDot(String comment) {
        return GraphvizPrinter.toDot(comment, receptorFields, associativeFields, effectorFields, feedbackFields);
    }

    @Override
    public long totalBytes() {
        int numField = numFields();
        int maxEdge = maxNeighbours();

        return NeuronFieldModel.calculateSize(numField, maxEdge);
    }

    @Override
    public boolean isSuccess() {
        return errorMsg.isEmpty();
    }

    @Override
    public List<String> getErrorMessage() {
        return errorMsg;
    }

    @Override
    public Optional<NeuronField> build() {
        if (!isSuccess()) {
            return Optional.empty();
        }
        return null; // TODO
    }

    @Override
    public int maxNeighbours() {
        int max = 0;
        for (int i = 0; i < receptorFields.size(); i++) {
            max = Math.max(max, receptorFields.get(i).neighbours().size());
        }
        for (int i = 0; i < associativeFields.size(); i++) {
            max = Math.max(max, associativeFields.get(i).neighbours().size());
        }
        for (int i = 0; i < effectorFields.size(); i++) {
            max = Math.max(max, effectorFields.get(i).neighbours().size());
        }
        for (int i = 0; i < feedbackFields.size(); i++) {
            max = Math.max(max, feedbackFields.get(i).neighbours().size());
        }

        return max;
    }

    @Override
    public int numFields() {
        return receptorFields.size() + associativeFields.size() + effectorFields.size() + feedbackFields.size();
    }

    // --- helper ---

    private void validParamters(String type, int numFieldIds, int edges, float probability) {
        if (numFieldIds < 1) {
            errorMsg.add("The 'numFields' parameter in '" + type + "' must be greater than or equal to 1");
        }
        if (probability < 0.0f || probability > 1.0f) {
            errorMsg.add("The 'probability' parameter in '" + type + "' must be between 0 and 1");
        }
        if (numFieldIds == 2 && edges != 2) {
            errorMsg.add("The 'edges' parameter in '" + type + "' must be 2");
        }
        if (numFieldIds == 1 && edges != 0) {
            errorMsg.add("The 'edges' parameter in '" + type + "' must be 0");
        }
        if (numFieldIds >= 3 && edges > numFieldIds - 2) {
            errorMsg.add("The 'edges' parameter in '" + type + "' must be less than numFields - 2 = " + (numFieldIds - 2));
        }
    }

    private List<Node> smallWorld(List<Node> nodes, int k, float probability) {
        int n = nodes.size();
        if (n == 0) {
            return List.of();
        }
        if (n == 1) {
            return nodes;
        }

        k = Math.min(k, n - 1);
        int halfK = k / 2;
        if (halfK < 1) {
            halfK = 1;
        }

        List<Set<Integer>> adj = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adj.add(new HashSet<>());
        }

        for (int i = 0; i < n; i++) {
            for (int j = 1; j <= halfK; j++) {
                int target = (i + j) % n;
                adj.get(i).add(target);
                adj.get(target).add(i);
            }
        }

        Random rand = new Random();
        for (int i = 0; i < n; i++) {
            for (int j = 1; j <= halfK; j++) {
                if (rand.nextFloat() < probability) {
                    int newTarget = rand.nextInt(n);
                    if (newTarget != i && !adj.get(i).contains(newTarget)) {
                        adj.get(i).add(newTarget);
                        adj.get(newTarget).add(i);
                    }
                }
            }
        }

        return buildResult(nodes, adj);
    }

    private List<Node> buildResult(List<Node> originalNodes, List<Set<Integer>> adj) {
        List<Node> result = new ArrayList<>();
        int n = originalNodes.size();

        for (int i = 0; i < n; i++) {
            Node resNode = Node.of(originalNodes.get(i).id(), originalNodes.get(i).nodeType());
            for (int neighborIdx : adj.get(i)) {
                resNode.neighbours().add(originalNodes.get(neighborIdx).id());
            }
            result.add(resNode);
        }
        return result;
    }

    private static int even(int n) {
        return (n + 1) & ~1;
    }
}
