/// @(#)NeuronFieldBuilderImpl.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.uwe_hennig.snn.cerebro.contracts.FieldCompileResult;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;
import com.uwe_hennig.snn.contracts.core.NeuronFieldType;
import com.uwe_hennig.snn.graph.generator.contracts.IdGenerator;

/// NeuronFieldBuilderImpl
///
/// @author Uwe Hennig
///
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder, FieldCompileResult {
    record Node(int id, int nodeType, List<Integer> neighbours) {
        public static Node of(int id, int type) {
            return new Node(id, type, new ArrayList<>());
        }
    }

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
    public NeuronFieldBuilder connectReceptorFields(int edges) {
        // TODO
        return this;
    }

    @Override
    public NeuronFieldBuilder connectAssociativeFields(int edges) {
        // TODO
        return this;

    }

    @Override
    public NeuronFieldBuilder connectEffectorFields(int edges) {
        // TODO
        return this;

    }

    @Override
    public NeuronFieldBuilder connectFeedbackFields(int edges) {
        // TODO
        return this;
    }

    @Override
    public FieldCompileResult compile() {
        validate();
        receptorFields = smallWorld(receptorFields, receptorEdges, receptorProbability);
        associativeFields = smallWorld(associativeFields, associativeEdges, associativeProbability);
        effectorFields = smallWorld(effectorFields, effectorEdges, effectorProbability);
        feedbackFields = smallWorld(feedbackFields, feedbackEdges, feedbackProbability);
        return this;
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
        // TODO Auto-generated method stub class FieldCompileResult
        return null;
    }

    @Override
    public long totalBytes() {
        // TODO Auto-generated method stub class FieldCompileResult
        return 0;
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
    public int maxEdges() {
        // TODO
        return 0;
    }

    @Override
    public int numNodes() {
        // TODO
        return 0;
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
        if (n <= 0) {
            errorMsg.add("Empty fields found in small world graph!");
            return List.of();
        }
        if (n == 1) {
            return nodes;
        }
        if (n == 2) {
            nodes.get(0).neighbours.add(nodes.get(1).id);
            nodes.get(1).neighbours.add(nodes.get(0).id);
            return nodes;
        }

        Random rand = new Random();
        int halfK = k / 2;

        Node[] tempNodes = new Node[n];
        for (int i = 0; i < nodes.size(); i++) {
            tempNodes[i] = new Node(i, nodes.get(i).nodeType, new ArrayList<>());
        }

        // ring
        for (int i = 0; i < n; i++) {
            for (int j = 1; j <= halfK; j++) {
                int target = (i + j) % n;
                tempNodes[i].neighbours().add(target);
                tempNodes[target].neighbours().add(i);
            }
        }

        if (n > 3) {
            // rewire
            for (int i = 0; i < n; i++) {
                for (int j = 1; j <= halfK; j++) {
                    if (rand.nextFloat() < probability) {
                        int oldTarget = (i + j) % n;

                        int newTarget;
                        int retries = 3;
                        do {
                            newTarget = rand.nextInt(n);
                        } while ((newTarget == i || tempNodes[i].neighbours().contains(newTarget)) && retries-- > 0);

                        if (retries > 0) {
                            tempNodes[i].neighbours().remove(Integer.valueOf(oldTarget));
                            tempNodes[oldTarget].neighbours().remove(Integer.valueOf(i));

                            tempNodes[i].neighbours().add(newTarget);
                            tempNodes[newTarget].neighbours().add(i);
                        }
                    }
                }
            }
        }

        List<Node> result = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            Node currentTmpNode = tempNodes[i];
            Node resultNode = Node.of(nodes.get(i).id, nodes.get(i).nodeType);
            result.add(resultNode);

            for (int neighbourIds : currentTmpNode.neighbours()) {
                int id = nodes.get(neighbourIds).id;
                resultNode.neighbours.add(id);
            }
        }

        return result;
    }

    private static int even(int n) {
        return (n + 1) & ~1;
    }

    public static void main(String[] args) {
        NeuronFieldBuilderImpl nfbi = new NeuronFieldBuilderImpl();
        nfbi.withReceptorFields(1, 0, 0.1f).withAssociativeFields(2, 2, 1f).withEffectorFields(3, 1, 1f).withFeedbackFields(4, 2, 1f).compile();

        System.out.println("Errors: ");
        for (int i = 0; i < nfbi.errorMsg.size(); i++) {
            System.out.println(nfbi.errorMsg.get(i));
        }

        System.out.println("\nConnections: ");
        System.out.println("--- receptors ---");
        for (int i = 0; i < nfbi.receptorFields.size(); i++) {
            System.out.println(nfbi.receptorFields.get(i));
        }
        System.out.println();

        System.out.println("--- associative ---");
        for (int i = 0; i < nfbi.associativeFields.size(); i++) {
            System.out.println(nfbi.associativeFields.get(i));
        }
        System.out.println();

        System.out.println("--- effectors ---");
        for (int i = 0; i < nfbi.effectorFields.size(); i++) {
            System.out.println(nfbi.effectorFields.get(i));
        }
        System.out.println();

        System.out.println("--- feedbacks ---");
        for (int i = 0; i < nfbi.feedbackFields.size(); i++) {
            System.out.println(nfbi.feedbackFields.get(i));
        }
        System.out.println();
    }
}
