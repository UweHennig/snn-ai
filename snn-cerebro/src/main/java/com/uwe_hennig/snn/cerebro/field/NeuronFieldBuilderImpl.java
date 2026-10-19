/// @(#)NeuronFieldBuilderImpl.java
/// Copyright (c) 2026 Uwe Hennig
/// All rights reserved.
package com.uwe_hennig.snn.cerebro.field;

import java.nio.file.Path;

import com.uwe_hennig.snn.cerebro.contracts.FieldNetwork;
import com.uwe_hennig.snn.cerebro.contracts.NeuronFieldBuilder;

/// NeuronFieldBuilderImpl
///
/// @author Uwe Hennig
///
public class NeuronFieldBuilderImpl implements NeuronFieldBuilder, FieldNetwork {
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
        return this;
    }

    @Override
    public NeuronFieldBuilder wireEffectors(int inDegree) {
        assert inDegree >= 1 : "invalid effector in degree";
        this.effectorInDegree = inDegree;
        return this;
    }

    @Override
    public NeuronFieldBuilder wireAssociative(int inDegree, int outDegree) {
        assert inDegree >= 1 : "invalid associative in degree";
        assert outDegree >= 1 : "invalid associative out degree";

        this.associativeInDegree = inDegree;
        this.associativeOutDegree = outDegree;

        return this;
    }

    @Override
    public NeuronFieldBuilder wireFeedback(int inDegree, int outDegree) {
        assert inDegree >= 1 : "invalid feedback in degree";
        assert outDegree >= 1 : "invalid feedback out degree";

        this.feedbackInDegree = inDegree;
        this.feedbackOutDegree = outDegree;

        return this;
    }

    @Override
    public FieldNetwork compile() {
        // TODO Auto-generated method stub class NeuronFieldBuilder
        return this;
    }

    // --- FieldNetwork methods ---

    @Override
    public void toDot(Path path) {
        // TODO Auto-generated method stub class FieldNetwork

    }

    @Override
    public long totalBytes() {
        // TODO Auto-generated method stub class FieldNetwork
        return 0;
    }

    @Override
    public NeuronField build() {
        // TODO Auto-generated method stub class FieldNetwork
        return null;
    }
}
